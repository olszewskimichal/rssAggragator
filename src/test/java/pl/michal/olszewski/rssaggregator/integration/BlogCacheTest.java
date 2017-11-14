package pl.michal.olszewski.rssaggregator.integration;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;
import pl.michal.olszewski.rssaggregator.service.BlogService;

@Transactional
public class BlogCacheTest extends IntegrationTestBase {

  @Autowired
  private BlogService service;

  @Autowired
  private BlogRepository blogRepository;

  @Test
  public void shouldReturnTheSameCollectionFromCache() {
    List<Blog> allBlogs = service.getAllBlogs();
    List<Blog> cacheBlogs = service.getAllBlogs();
    assertSame(allBlogs, cacheBlogs);
  }

  @Test
  public void shouldAfterEvictCacheAndReturnNotTheSameCollection() {
    List<Blog> allBlogs = service.getAllBlogs();
    service.evictBlogCache();
    List<Blog> cacheBlogs = service.getAllBlogs();
    assertNotSame(allBlogs, cacheBlogs);
  }

  @Test
  public void shouldAfterCreateNewBlogAndReturnNotTheSameCollection() {
    List<Blog> allBlogs = service.getAllBlogs();
    service.createBlog(BlogDTO.builder().name("nazwa2").build());
    List<Blog> cacheBlogs = service.getAllBlogs();
    assertNotSame(allBlogs, cacheBlogs);
  }

  @Test
  public void shouldAfterDeleteNewBlogAndReturnNotTheSameCollection() {
    Blog blog = service.createBlog(BlogDTO.builder().name("nazwa2").build());

    List<Blog> allBlogs = service.getAllBlogs();
    List<Blog> cacheBlogs = service.getAllBlogs();
    assertSame(allBlogs, cacheBlogs);
    service.deleteBlog(blog.getId());

    cacheBlogs = service.getAllBlogs();
    assertNotSame(allBlogs, cacheBlogs);
  }


  @Test
  public void shouldFindByNameFromCache() {
    service.createBlog(BlogDTO.builder().name("nazwa").build());
    Optional<Blog> byName = blogRepository.findByName("nazwa");
    Optional<Blog> cachedDTO = blogRepository.findByName("nazwa");
    assertSame(byName.get(), cachedDTO.get());
  }

  @Test
  public void shouldNotEvictFindByNameWhenCreate() {
    service.createBlog(BlogDTO.builder().name("nazwa").build());
    Optional<Blog> byName = blogRepository.findByName("nazwa");
    service.createBlog(BlogDTO.builder().name("nazwa2").build());
    Optional<Blog> cachedDTO = blogRepository.findByName("nazwa");
    assertSame(byName.get(), cachedDTO.get());
  }

}
