package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.ogtags.OgTagInfoUpdater;
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
  private final OgTagInfoUpdater ogTagInfoUpdater;

  BlogService(
      BlogFinder blogFinder,
      BlogWorker blogUpdater,
      @Qualifier("blogCache") Cache<String, BlogDTO> blogCache,
      BlogValidation blogValidation,
      OgTagInfoUpdater ogTagInfoUpdater
  ) {
    this.blogFinder = blogFinder;
    this.blogUpdater = blogUpdater;
    this.blogCache = blogCache;
    this.blogValidation = blogValidation;
    this.ogTagInfoUpdater = ogTagInfoUpdater;
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
    Blog blog = blogFinder.findByFeedURL(blogDTO.getFeedURL())
        .orElseGet(() -> createBlog(blogDTO));
    return Mono.justOrEmpty(BlogToDtoMapper.mapToBlogDto(blog));
  }

  void deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    BlogAggregationDTO blogAggregationDTO = blogFinder.getBlogWithCount(id)
        .orElseThrow(() -> new BlogNotFoundException(id));
    if (blogAggregationDTO.getBlogItemsCount() == 0) {
      blogUpdater.deleteBlogById(blogAggregationDTO.getBlogId());
      blogCache.invalidate(id);
      return;
    }
    blogFinder.findById(id).ifPresent(Blog::deactivate);
  }

  Mono<BlogDTO> getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return Mono.justOrEmpty(blogCache.getIfPresent(id))
        .switchIfEmpty(Mono.defer(() -> blogFinder.findById(id)
            .map(BlogToDtoMapper::mapToBlogDto)
            .map(Mono::just)
            .orElseGet(() -> Mono.error(new BlogNotFoundException(id)))));
           /* .doOnEach(blogDTO -> log.trace("getBlogDTObyId {}", id))
            .doOnSuccess(result -> blogCache.put(result.getId(), result))));*/
  }

  void evictAndRecreateBlogCache() {
    log.debug("Czyszcze cache dla blogów");
    blogCache.invalidateAll();
    blogFinder.findAll()
        .forEach(this::putToCache);
  }

  BlogDTO updateBlog(UpdateBlogDTO updateBlogDTO, String blogId) {
    log.debug("Aktualizacja bloga {}", updateBlogDTO.getName());
    blogValidation.validate(updateBlogDTO.getLink(), updateBlogDTO.getFeedURL());
    Blog blog = blogFinder.findById(blogId)
        .orElseThrow(() -> new BlogNotFoundException(blogId));
    Blog updatedBlog = updateBlog(blog, updateBlogDTO);
    return BlogToDtoMapper.mapToBlogDto(updatedBlog);
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

  private Blog createBlog(CreateBlogDTO blogDTO) {
    blogValidation.validate(blogDTO.getLink(), blogDTO.getFeedURL());
    log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());
    Blog newBlog = ogTagInfoUpdater.updateItemByOgTagInfo(blogUpdater.createNewBlog(blogDTO));
    putToCache(newBlog);
    return newBlog;
  }

  private Blog updateBlog(Blog blogFromDb, UpdateBlogDTO blogInfoFromRSS) {
    log.debug("aktualizuje bloga {}", blogFromDb.getName());
    Blog blog = blogUpdater.updateBlogFromDTO(blogFromDb, blogInfoFromRSS);
    ogTagInfoUpdater.updateItemByOgTagInfo(blog);
    putToCache(blog);
    return blog;
  }
}
