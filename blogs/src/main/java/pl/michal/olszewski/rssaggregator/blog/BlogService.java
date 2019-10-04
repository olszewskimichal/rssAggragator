package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagBlogUpdater;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class BlogService {

  private final BlogFinder blogFinder;
  private final BlogWorker blogUpdater;
  private final Cache<String, BlogDTO> blogCache;
  private final BlogValidation blogValidation;
  private final OgTagBlogUpdater ogTagBlogUpdater;

  public BlogService(
      BlogFinder blogFinder,
      BlogWorker blogUpdater,
      @Qualifier("blogCache") Cache<String, BlogDTO> blogCache,
      BlogValidation blogValidation,
      OgTagBlogUpdater ogTagBlogUpdater
  ) {
    this.blogFinder = blogFinder;
    this.blogUpdater = blogUpdater;
    this.blogCache = blogCache;
    this.blogValidation = blogValidation;
    this.ogTagBlogUpdater = ogTagBlogUpdater;
  }

  public Flux<BlogDTO> getAllBlogDTOs() {
    log.debug("pobieram wszystkie blogi w postaci DTO");
    var dtoFlux = Flux.fromIterable(blogCache.asMap().values())
        .switchIfEmpty(Flux.defer(() -> blogFinder.findAll()
            .map(BlogToDtoMapper::mapToBlogDto)
            .doOnNext(blog -> blogCache.put(blog.getId(), blog)))
            .cache());
    return dtoFlux
        .doOnEach(blogDTO -> log.trace("getAllBlogDTOs {}", blogDTO));
  }

  Mono<BlogDTO> getBlogOrCreate(CreateBlogDTO blogDTO) {
    log.debug("Tworzenie nowego bloga {}", blogDTO.getFeedURL());
    return blogFinder.findByFeedURL(blogDTO.getFeedURL())
        .switchIfEmpty(Mono.defer(() -> createBlog(blogDTO)))
        .map(blog -> new BlogDTO(
            blog.getId(),
            blog.getBlogURL(),
            blog.getDescription(),
            blog.getName(),
            blog.getFeedURL(),
            blog.getPublishedDate(),
            blog.getImageUrl()));
  }

  Mono<Void> deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    return blogFinder.getBlogWithCount(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .flatMap(blog -> {
          if (blog.getBlogItemsCount() == 0) {
            return blogUpdater.deleteBlogById(blog.getBlogId())
                .doOnSuccess(v -> blogCache.invalidate(id));
          }
          return blogFinder.findById(id)
              .flatMap(blogById -> {
                blogById.deactivate();
                return Mono.empty();
              });
        });
  }

  Mono<BlogDTO> getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return Mono.justOrEmpty(blogCache.getIfPresent(id))
        .switchIfEmpty(Mono.defer(() -> blogFinder.findById(id).cache()
            .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
            .map(BlogToDtoMapper::mapToBlogDto)
            .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id))
            .doOnSuccess(result -> blogCache.put(result.getId(), result))));
  }

  void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
    blogCache.invalidateAll();
  }

  Mono<BlogDTO> updateBlog(UpdateBlogDTO blogDTO, String blogId) {
    log.debug("Aktualizacja bloga {}", blogDTO.getName());
    blogValidation.validate(blogDTO.getLink(), blogDTO.getFeedURL());
    return blogFinder.findById(blogId).cache()
        .switchIfEmpty(Mono.error(new BlogNotFoundException(blogId)))
        .flatMap(blog -> updateBlog(blog, blogDTO))
        .map(blog -> new BlogDTO(
            blog.getId(),
            blog.getBlogURL(),
            blog.getDescription(),
            blog.getName(),
            blog.getFeedURL(),
            blog.getPublishedDate(),
            blog.getImageUrl()))
        .doOnSuccess(updatedBlog -> blogCache.invalidate(updatedBlog.getId()));
  }

  private void putToCache(Blog updatedBlog) {
    blogCache.put(
        updatedBlog.getId(),
        new BlogDTO(
            updatedBlog.getId(),
            updatedBlog.getBlogURL(),
            updatedBlog.getDescription(),
            updatedBlog.getName(),
            updatedBlog.getFeedURL(),
            updatedBlog.getPublishedDate(),
            updatedBlog.getImageUrl())
    );
  }

  private Mono<Blog> createBlog(CreateBlogDTO blogDTO) {
    blogValidation.validate(blogDTO.getLink(), blogDTO.getFeedURL());
    log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());
    return blogUpdater.createNewBlog(blogDTO)
        .map(ogTagBlogUpdater::updateBlogByOgTagInfo)
        .doOnNext(this::putToCache);
  }

  private Mono<Blog> updateBlog(Blog blogFromDb, UpdateBlogDTO blogInfoFromRSS) {
    log.debug("aktualizuje bloga {}", blogFromDb.getName());
    return blogUpdater.updateBlogFromDTO(blogFromDb, blogInfoFromRSS)
        .map(ogTagBlogUpdater::updateBlogByOgTagInfo)
        .doOnNext(this::putToCache);
  }
}
