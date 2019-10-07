package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;

class BlogCacheTest extends IntegrationTestBase {

  private Blog blog;
  @Autowired
  private BlogService service;
  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "blog");
    blog = mongoTemplate.save(new BlogBuilder().blogURL("nazwa").feedURL("nazwa").name("nazwa").build());
    service.evictBlogCache();
  }

  @Test
  void shouldReturnTheSameCollectionFromCache() {
    //given
    Flux<BlogDTO> allBlogs = service.getAllBlogDTOs();
    List<BlogDTO> blogList = allBlogs.collectList().block();

    //when
    Flux<BlogDTO> cacheBlogs = service.getAllBlogDTOs();

    //then
    assertThat(blogList).hasSameElementsAs(cacheBlogs.toIterable());
  }

  @Test
  void shouldAfterEvictCacheAndReturnNotTheSameCollection() {
    //given
    Flux<BlogDTO> allBlogs = service.getAllBlogDTOs();
    List<BlogDTO> blogList = allBlogs.collectList().block();

    //when
    service.evictBlogCache();
    Flux<BlogDTO> blogs = service.getAllBlogDTOs();

    //then
    assertNotSame(allBlogs, blogs);
    assertThat(blogList).containsExactlyElementsOf(blogs.toIterable());
  }

  @Test
  void shouldAfterCreateNewBlogAndReturnNotTheSameCollection() {
    //given
    Flux<BlogDTO> allBlogs = service.getAllBlogDTOs();
    List<BlogDTO> blogList = allBlogs.collectList().block();

    //when
    service.getBlogOrCreate(new CreateBlogDTOBuilder().name("nazwa2").build()).block();
    Flux<BlogDTO> blogs = service.getAllBlogDTOs();

    //then
    assertNotSame(allBlogs, blogs);
    assertThat(blogList).isNotEqualTo(blogs.toIterable());
  }

  @Test
  void shouldAfterDeleteNewBlogAndReturnNotTheSameCollection() {
    //given
    Flux<BlogDTO> allBlogs = service.getAllBlogDTOs();
    List<BlogDTO> blogList = allBlogs.collectList().block();

    //when
    service.deleteBlog(blog.getId()).block();
    Flux<BlogDTO> blogs = service.getAllBlogDTOs();

    //then
    assertNotSame(allBlogs, blogs);
    assertThat(blogList).isNotEqualTo(blogs.toIterable());
  }

}
