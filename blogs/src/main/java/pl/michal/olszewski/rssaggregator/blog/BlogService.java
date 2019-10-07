package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BlogService {

  private static final Logger log = LoggerFactory.getLogger(BlogService.class);

  private final BlogFinder blogFinder;
  private final BlogWorker blogUpdater;
  private final Cache<String, BlogDTO> blogCache;

  BlogService(
      BlogFinder blogFinder,
      BlogWorker blogUpdater,
      @Qualifier("blogCache") Cache<String, BlogDTO> blogCache) {
    this.blogFinder = blogFinder;
    this.blogUpdater = blogUpdater;
    this.blogCache = blogCache;
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
        .map(blog -> new BlogDTOBuilder().id(blog.getId()).link(blog.getBlogURL()).description(blog.getDescription()).name(blog.getName()).feedURL(blog.getFeedURL())
            .publishedDate(blog.getPublishedDate()).build());
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

  Mono<BlogDTO> updateBlog(UpdateBlogDTO blogDTO) {
    log.debug("Aktualizacja bloga {}", blogDTO.getName());
    return getBlogByFeedUrl(blogDTO.getFeedURL())
        .flatMap(blog -> updateBlog(blog, blogDTO))
        .map(blog -> new BlogDTOBuilder().id(blog.getId()).link(blog.getBlogURL()).description(blog.getDescription()).name(blog.getName()).feedURL(blog.getFeedURL())
            .publishedDate(blog.getPublishedDate()).build())
        .doOnSuccess(updatedBlog -> blogCache.invalidate(updatedBlog.getId()));
  }

  private void putToCache(Blog updatedBlog) {
    blogCache.put(
        updatedBlog.getId(),
        new BlogDTOBuilder().id(updatedBlog.getId()).link(updatedBlog.getBlogURL()).description(updatedBlog.getDescription()).name(updatedBlog.getName()).feedURL(updatedBlog.getFeedURL())
            .publishedDate(updatedBlog.getPublishedDate()).build()
    );
  }

  private Mono<Blog> createBlog(CreateBlogDTO blogDTO) {
    log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());
    return blogUpdater.createNewBlog(blogDTO)
        .doOnNext(this::putToCache);
  }

  private Mono<Blog> getBlogByFeedUrl(String feedUrl) {
    log.debug("getBlogByFeedUrl feedUrl {}", feedUrl);
    return blogFinder.findByFeedURL(feedUrl)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(feedUrl)));
  }

  private Mono<Blog> updateBlog(Blog blogFromDb, UpdateBlogDTO blogInfoFromRSS) {
    log.debug("aktualizuje bloga {}", blogFromDb.getName());
    return blogUpdater.updateBlogFromDTO(blogFromDb, blogInfoFromRSS)
        .doOnNext(this::putToCache);
  }
}
