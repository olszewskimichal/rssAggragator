package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class BlogCacheTest extends IntegrationTestBase {

  private Blog blog;
  @Autowired
  private BlogService service;
  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.dropCollection(Blog.class);
    blog = mongoTemplate.save(Blog.builder().blogURL("nazwa").feedURL("nazwa").name("nazwa").build());
  }

  @Test
  void shouldReturnTheSameCollectionFromCache() {
    //given
    Flux<Blog> allBlogs = service.getAllBlogs();
    List<Blog> blogList = allBlogs.collectList().block();

    //when
    Flux<Blog> cacheBlogs = service.getAllBlogs();

    //then
    assertSame(allBlogs, cacheBlogs);
    assertThat(blogList).containsExactlyElementsOf(cacheBlogs.toIterable());
  }

  @Test
  void shouldAfterEvictCacheAndReturnNotTheSameCollection() {
    //given
    Flux<Blog> allBlogs = service.getAllBlogs();
    List<Blog> blogList = allBlogs.collectList().block();

    //when
    service.evictBlogCache();
    Flux<Blog> blogs = service.getAllBlogs();

    //then
    assertNotSame(allBlogs, blogs);
    assertThat(blogList).containsExactlyElementsOf(blogs.toIterable());
  }

  @Test
  void shouldAfterCreateNewBlogAndReturnNotTheSameCollection() {
    //given
    Flux<Blog> allBlogs = service.getAllBlogs();
    List<Blog> blogList = allBlogs.collectList().block();

    //when
    service.createBlog(BlogDTO.builder().name("nazwa2").build()).block();
    Flux<Blog> blogs = service.getAllBlogs();

    //then
    assertNotSame(allBlogs, blogs);
    assertThat(blogList).isNotEqualTo(blogs.toIterable());
  }

  @Test
  void shouldAfterDeleteNewBlogAndReturnNotTheSameCollection() {
    //given
    Flux<Blog> allBlogs = service.getAllBlogs();
    List<Blog> blogList = allBlogs.collectList().block();

    //when
    service.deleteBlog(blog.getId()).block();
    Flux<Blog> blogs = service.getAllBlogs();

    //then
    assertNotSame(allBlogs, blogs);
    assertThat(blogList).isNotEqualTo(blogs.toIterable());
  }


  @Test
  void shouldFindByNameFromCache() {
    //given
    Mono<BlogInfoDTO> byName = service.getBlogDTOByName("nazwa");
    BlogInfoDTO blogDTO = byName.block();

    //when
    Mono<BlogInfoDTO> cachedDTO = service.getBlogDTOByName("nazwa");

    //then
    assertSame(byName, cachedDTO);
    assertEquals(blogDTO, cachedDTO.block());
  }

  @Test
  void shouldNotEvictFindByNameWhenCreate() {
    //given
    Mono<BlogInfoDTO> byName = service.getBlogDTOByName("nazwa");
    BlogInfoDTO blogDTO = byName.block();

    //when
    service.createBlog(BlogDTO.builder().name("nazwa2").build()).block();
    Mono<BlogInfoDTO> cachedDTO = service.getBlogDTOByName("nazwa");

    //then
    assertSame(byName, cachedDTO);
    assertEquals(blogDTO, cachedDTO.block());
  }

}
