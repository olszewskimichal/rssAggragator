package pl.michal.olszewski.rssaggregator.blog;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    Mono<BlogDTO> byName = service.getBlogDTOByName("nazwa");
    Mono<BlogDTO> cachedDTO = service.getBlogDTOByName("nazwa");
    assertSame(byName, cachedDTO);
  }

  @Test
  void shouldNotEvictFindByNameWhenCreate() {
    service.createBlog(BlogDTO.builder().name("nazwa").build());
    Mono<BlogDTO> byName = service.getBlogDTOByName("nazwa");
    service.createBlog(BlogDTO.builder().name("nazwa2").build());
    Mono<BlogDTO> cachedDTO = service.getBlogDTOByName("nazwa");
    assertSame(byName, cachedDTO);
  }

}
