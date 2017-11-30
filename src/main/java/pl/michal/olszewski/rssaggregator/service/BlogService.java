package pl.michal.olszewski.rssaggregator.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.exception.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

@Service
@Transactional
@Slf4j
public class BlogService {

  private final BlogRepository blogRepository;

  public BlogService(BlogRepository blogRepository) {
    this.blogRepository = blogRepository;
  }

  @CacheEvict(value = {"blogs", "blogsName", "blogsDTO"}, allEntries = true)
  public Blog createBlog(BlogDTO blogDTO) {
    Optional<Blog> byFeedURL = blogRepository.findByFeedURL(blogDTO.getFeedURL());
    if (!byFeedURL.isPresent()) {
      log.debug("Dodaje nowy blog o nazwie {}", blogDTO.getName());

      Blog blog = new Blog(blogDTO.getLink(), blogDTO.getDescription(), blogDTO.getName(), blogDTO.getFeedURL(), blogDTO.getPublishedDate(), null);
      blogDTO.getItemsList().stream()
          .map(Item::new)
          .forEach(blog::addItem);
      blogRepository.save(blog);
      return blog;
    }
    return byFeedURL.get();
  }

  private Blog getBlogByName(String name) {
    return blogRepository.findByName(name).orElseThrow(() -> new BlogNotFoundException(name));
  }

  private Blog getBlogByFeedUrl(String feedUrl) {
    return blogRepository.findByFeedURL(feedUrl).orElseThrow(() -> new BlogNotFoundException(feedUrl));
  }


  @Transactional
  public Blog updateBlog(BlogDTO blogDTO) {
    Blog blog = getBlogByFeedUrl(blogDTO.getFeedURL());
    blogDTO.getItemsList().stream()
        .filter(item -> item.getDate().isAfter(blog.getLastUpdateDate() == null ? Instant.MIN : blog.getLastUpdateDate()))
        .map(Item::new)
        .filter(v -> !blog.getItems().stream().parallel().map(Item::getLink).collect(Collectors.toSet()).contains(v.getLink()))
        .forEach(blog::addItem);
    blog.updateFromDto(blogDTO);
    return blog;
  }

  @Cacheable("blogs")
  public List<Blog> getAllBlogs() {
    log.debug("Pobieram wszystkie blogi");
    return blogRepository.findStreamAll().parallel().collect(Collectors.toList());
  }

  @CacheEvict(value = {"blogs", "blogsName", "blogsDTO"}, allEntries = true)
  public boolean deleteBlog(Long id) {
    log.debug("Usuwam bloga o id {}", id);
    Blog blog = blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id));
    blogRepository.delete(blog);
    return true;
  }

  @Transactional(readOnly = true)
  public BlogDTO getBlogDTOById(Long id) {
    log.debug("pobieram bloga w postaci DTO o id {}", id);
    Blog blogById = blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id));
    return new BlogDTO(blogById.getBlogURL(), blogById.getDescription(), blogById.getName(), blogById.getFeedURL(), blogById.getPublishedDate(), extractItems(blogById));
  }

  @Cacheable("blogsName")
  @Transactional(readOnly = true)
  public BlogDTO getBlogDTOByName(String name) {
    log.debug("pobieram bloga w postaci DTO o nazwie {}", name);
    Blog blog = getBlogByName(name);
    return new BlogDTO(blog.getBlogURL(), blog.getDescription(), blog.getName(), blog.getFeedURL(), blog.getPublishedDate(), extractItems(blog));
  }

  @Cacheable("blogsDTO")
  @Transactional(readOnly = true)
  public List<BlogDTO> getAllBlogDTOs(Integer limit) {
    log.debug("pobieram wszystkie blogi w postaci DTO z limitem {}", limit);
    return getAllBlogs().stream()
        .parallel()
        .limit(getLimit(limit))
        .map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), extractItems(v)))
        .collect(Collectors.toList());
  }

  private List<ItemDTO> extractItems(Blog v) {
    return v.getItems().stream().parallel().map(item -> new ItemDTO(item.getTitle(), item.getDescription(), item.getLink(), item.getDate(), item.getAuthor())).collect(Collectors.toList());
  }

  private int getLimit(final Integer size) {
    return (Objects.isNull(size) ? 20 : size);
  }

  @CacheEvict(value = {"blogs", "blogsDTO", "blogsName"}, allEntries = true)
  public void evictBlogCache() {
    log.debug("Czyszcze cache dla blogów");
  }
}
