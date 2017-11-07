package pl.michal.olszewski.rssaggregator.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
public class BlogService {

  private final BlogRepository blogRepository;

  public BlogService(BlogRepository blogRepository) {
    this.blogRepository = blogRepository;
  }

  @CacheEvict(value = {"blogs", "blogsURL"})
  public Blog createBlog(BlogDTO blogDTO) {
    Blog blog = new Blog(blogDTO.getLink(), blogDTO.getDescription(), blogDTO.getName(), blogDTO.getFeedURL(), blogDTO.getPublishedDate());
    blogDTO.getItemsList().stream()
        .map(Item::new)
        .forEach(blog::addItem);
    blogRepository.save(blog);
    return blog;
  }

  @Cacheable("blogsURL")
  public Blog getBlogByURL(String url) {
    return blogRepository.findByBlogURL(url)
        .orElseThrow(() -> new BlogNotFoundException(url));
  }

  public Blog getBlogById(Long id) {
    return blogRepository.findById(id)
        .orElseThrow(() -> new BlogNotFoundException(id));
  }

  public Blog updateBlog(BlogDTO blogDTO) {
    Blog blog = getBlogByURL(blogDTO.getLink());
    blog.updateFromDto(blogDTO);
    blogDTO.getItemsList().stream()
        .map(Item::new)
        .filter(v -> !blog.getItems().contains(v))
        .forEach(blog::addItem);
    blogRepository.save(blog);
    return blog;
  }

  @Cacheable("blogs")
  public List<Blog> getAllBlogs() {
    return blogRepository.findStreamAll().collect(Collectors.toList());
  }

  @CacheEvict(value = {"blogs", "blogsURL"})
  public boolean deleteBlog(Long id) {
    Blog blog = blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id));
    blogRepository.delete(blog);
    return true;
  }

  public BlogDTO getBlogDTOById(Long id) {
    Blog blogById = getBlogById(id);
    return new BlogDTO(blogById.getBlogURL(), blogById.getDescription(), blogById.getName(), blogById.getFeedURL(), blogById.getPublishedDate(), extractItems(blogById));
  }

  @Cacheable("blogs")
  public List<BlogDTO> getAllBlogDTOs(Integer limit) {
    return blogRepository.findStreamAll()
        .limit(getLimit(limit))
        .map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), extractItems(v)))
        .collect(Collectors.toList());
  }

  private List<ItemDTO> extractItems(Blog v) {
    return v.getItems().stream().map(item -> new ItemDTO(item.getTitle(), item.getDescription(), item.getLink(), item.getDate(), item.getAuthor())).collect(Collectors.toList());
  }

  private int getLimit(final Integer size) {
    return (Objects.isNull(size) ? 20 : size);
  }

}
