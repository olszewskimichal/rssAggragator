package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagBlogUpdater;
import pl.michal.olszewski.rssaggregator.util.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class BlogService {

  private static final Logger log = LoggerFactory.getLogger(BlogService.class);

  private final BlogFinder blogFinder;
  private final BlogWorker blogUpdater;
  private final Cache<String, BlogDTO> blogCache;
  private final BlogValidation blogValidation;
  private final OgTagBlogUpdater ogTagBlogUpdater;

  BlogService(
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

  public Mono<PageBlogDTO> getAllBlogDTOs(Integer limit, Integer page) {
    Page pageable = new Page(limit, page);
    log.debug("pobieram wszystkie blogi w postaci DTO {}", pageable);
    PageRequest pageRequest = PageRequest.of(pageable.getPageForSearch(), pageable.getLimit());
    Mono<Long> count = Mono.just(blogCache.estimatedSize());
    Mono<List<BlogDTO>> pagedBlog = Flux.fromIterable(blogCache.asMap().values())
        .buffer(pageRequest.getPageSize())
        .elementAt(pageRequest.getPageNumber(), new ArrayList<>());
    return count.zipWith(pagedBlog, (countBlogs, blogs) -> new PageBlogDTO(blogs, countBlogs));
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

  void evictAndRecreateBlogCache() {
    log.debug("Czyszcze cache dla blogów");
    blogCache.invalidateAll();
    List<Blog> block = blogFinder.findAll()
        .doOnNext(this::putToCache)
        .collectList()
        .block();
  }

  Mono<BlogDTO> updateBlog(UpdateBlogDTO blogDTO, String blogId) {
    log.debug("Aktualizacja bloga {}", blogDTO.getName());
    blogValidation.validate(blogDTO.getLink(), blogDTO.getFeedURL());
    return Mono.justOrEmpty(blogCache.getIfPresent(blogId))
        .switchIfEmpty(Mono.defer(() -> blogFinder.findById(blogId).cache()
            .switchIfEmpty(Mono.error(new BlogNotFoundException(blogId)))
            .flatMap(blog -> updateBlog(blog, blogDTO))
            .map(blog -> new BlogDTO(
                blog.getId(),
                blog.getBlogURL(),
                blog.getDescription(),
                blog.getName(),
                blog.getFeedURL(),
                blog.getPublishedDate(),
                blog.getImageUrl()))));
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
