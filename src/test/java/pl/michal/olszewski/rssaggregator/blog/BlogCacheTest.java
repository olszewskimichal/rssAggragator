package pl.michal.olszewski.rssaggregator.blog;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class BlogCacheTest extends IntegrationTestBase {

  @Autowired
  private BlogService service;

  @Test
  void shouldReturnTheSameCollectionFromCache() {
    Flux<Blog> allBlogs = service.getAllBlogs();
    Flux<Blog> cacheBlogs = service.getAllBlogs();
    assertSame(allBlogs, cacheBlogs);  //TODO sprawdzic czy Same na FLuxie dziala
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
    service.createBlog(BlogDTO.builder().name("nazwa2").build()); //TODO block albo jakaas alternatywa bo to wcale nic nie sprawdza
    Flux<Blog> cacheBlogs = service.getAllBlogs();
    assertNotSame(allBlogs, cacheBlogs);
  }

  @Test
  void shouldAfterDeleteNewBlogAndReturnNotTheSameCollection() {
    Blog blog = service.createBlog(BlogDTO.builder().name("nazwa2").build()).block();

    Flux<Blog> allBlogs = service.getAllBlogs();
    Flux<Blog> cacheBlogs = service.getAllBlogs();
    assertSame(allBlogs, cacheBlogs);
    service.deleteBlog(blog.getId());  //TODO block lub cos podobnego bo nie mam pewnosci czy usunieto

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
