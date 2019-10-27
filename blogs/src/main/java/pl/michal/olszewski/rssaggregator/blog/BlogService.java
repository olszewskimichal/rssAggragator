package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.ogtags.OgTagInfoUpdater;
import pl.michal.olszewski.rssaggregator.util.Page;

@Service
class BlogService {

  private static final Logger log = LoggerFactory.getLogger(BlogService.class);

  private final BlogRepository blogRepository;
  private final BlogWorker blogUpdater;
  private final Cache<String, BlogDTO> blogCache;
  private final BlogValidation blogValidation;
  private final OgTagInfoUpdater ogTagInfoUpdater;

  BlogService(
      BlogRepository blogRepository,
      BlogWorker blogUpdater,
      @Qualifier("blogCache") Cache<String, BlogDTO> blogCache,
      BlogValidation blogValidation,
      OgTagInfoUpdater ogTagInfoUpdater
  ) {
    this.blogRepository = blogRepository;
    this.blogUpdater = blogUpdater;
    this.blogCache = blogCache;
    this.blogValidation = blogValidation;
    this.ogTagInfoUpdater = ogTagInfoUpdater;
  }

  public PageBlogDTO getAllBlogDTOs(Integer limit, Integer page) {
    Page pageable = new Page(limit, page);
    log.debug("pobieram wszystkie blogi w postaci DTO {}", pageable);
    PageRequest pageRequest = PageRequest.of(pageable.getPageForSearch(), pageable.getLimit());
    long count = blogCache.estimatedSize();
    List<BlogDTO> blogDTOS = blogCache.asMap().values().stream()
        .skip(pageRequest.getPageNumber() * pageRequest.getPageSize())
        .limit(pageRequest.getPageSize())
        .collect(Collectors.toList());
    return new PageBlogDTO(blogDTOS, count);
  }

  BlogDTO getBlogOrCreate(CreateBlogDTO blogDTO) {
    log.debug("Tworzenie nowego bloga {}", blogDTO.getFeedURL());
    Blog blog = blogRepository.findByFeedURL(blogDTO.getFeedURL())
        .orElseGet(() -> createBlog(blogDTO));
    return BlogToDtoMapper.mapToBlogDto(blog);
  }

  void deleteBlog(String id) {
    log.debug("Usuwam bloga o id {}", id);
    BlogAggregationDTO blogAggregationDTO = blogRepository.getBlogWithCount(id)
        .orElseThrow(() -> new BlogNotFoundException(id));
    if (blogAggregationDTO.getBlogItemsCount() == 0) {
      blogUpdater.deleteBlogById(blogAggregationDTO.getBlogId());
      blogCache.invalidate(id);
      return;
    }
    blogRepository.findById(id).ifPresent(Blog::deactivate);
  }

  BlogDTO getBlogDTOById(String id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    return Optional.ofNullable(blogCache.getIfPresent(id))
        .orElseGet(() -> blogRepository.findById(id).map(BlogToDtoMapper::mapToBlogDto)
            .orElseThrow(() -> new BlogNotFoundException(id)));
  }

  BlogDTO updateBlog(UpdateBlogDTO updateBlogDTO, String blogId) {
    log.debug("Aktualizacja bloga {}", updateBlogDTO.getName());
    blogValidation.validate(updateBlogDTO.getLink(), updateBlogDTO.getFeedURL());
    Blog blog = blogRepository.findById(blogId)
        .orElseThrow(() -> new BlogNotFoundException(blogId));
    Blog updatedBlog = updateBlog(blog, updateBlogDTO);
    return BlogToDtoMapper.mapToBlogDto(updatedBlog);
  }

  void evictAndRecreateBlogCache() {
    log.debug("Czyszcze cache dla blogów");
    blogCache.invalidateAll();
    blogRepository.findAll()
        .forEach(this::putToCache);
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
