package pl.michal.olszewski.rssaggregator.blog;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.blog.BlogDTO;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogRepository;
import pl.michal.olszewski.rssaggregator.blog.BlogService;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Transactional
class BlogCacheTest extends IntegrationTestBase {

  @Autowired
  private BlogService service;

  @Autowired
  private BlogRepository blogRepository;

  @Test
  void shouldReturnTheSameCollectionFromCache() {
    Flux<Blog> allBlogs = service.getAllBlogs();
    Flux<Blog> cacheBlogs = service.getAllBlogs();
    assertSame(allBlogs, cacheBlogs);
  }

  @Test
  void shouldAfterEvictCacheAndReturnNotTheSameCollection() {
    Flux<Blog> allBlogs = service.getAllBlogs();
    service.evictBlogCache();
    Flux<Blog> cacheBlogs = service.getAllBlogs();
    assertNotSame(allBlogs, cacheBlogs);
  }

  @Test
  void shouldAfterCreateNewBlogAndReturnNotTheSameCollection() {
    Flux<Blog> allBlogs = service.getAllBlogs();
    service.createBlog(BlogDTO.builder().name("nazwa2").build());
    Flux<Blog> cacheBlogs = service.getAllBlogs();
    assertNotSame(allBlogs, cacheBlogs);
  }

  @Test
  void shouldAfterDeleteNewBlogAndReturnNotTheSameCollection() {
    Blog blog = service.createBlog(BlogDTO.builder().name("nazwa2").build()).block();

    Flux<Blog> allBlogs = service.getAllBlogs();
    Flux<Blog> cacheBlogs = service.getAllBlogs();
    assertSame(allBlogs, cacheBlogs);
    service.deleteBlog(blog.getId());

    cacheBlogs = service.getAllBlogs();
    assertNotSame(allBlogs, cacheBlogs);
  }


  @Test
  void shouldFindByNameFromCache() {
    service.createBlog(BlogDTO.builder().name("nazwa").build());
    Optional<Blog> byName = blogRepository.findByName("nazwa");
    Optional<Blog> cachedDTO = blogRepository.findByName("nazwa");
    assertSame(byName.get(), cachedDTO.get());
  }

  @Test
  void shouldNotEvictFindByNameWhenCreate() {
    service.createBlog(BlogDTO.builder().name("nazwa").build());
    Optional<Blog> byName = blogRepository.findByName("nazwa");
    service.createBlog(BlogDTO.builder().name("nazwa2").build());
    Optional<Blog> cachedDTO = blogRepository.findByName("nazwa");
    assertSame(byName.get(), cachedDTO.get());
  }

}
