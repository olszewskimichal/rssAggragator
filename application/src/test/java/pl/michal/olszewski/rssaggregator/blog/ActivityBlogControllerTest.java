package pl.michal.olszewski.rssaggregator.blog;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class ActivityBlogControllerTest extends IntegrationTestBase {

  @Autowired
  private BlogRepository blogRepository;

  @BeforeEach
  void setUp() {
    blogRepository.deleteAll();
  }

  @Test
  void shouldEnableBlogByApi() {
    //given
    Blog blog = givenBlog()
        .createAndSaveNewBlog();

    //when
    thenEnableBlogByApi(blog.getId());

    //then
    assertTrue(blogRepository.findById(blog.getId()).get().isActive());
  }

  @Test
  void shouldDisableBlogByApi() {
    //given
    Blog blog = givenBlog()
        .createAndSaveNewBlog();

    //when
    thenDisableBlogByApi(blog.getId());

    //then
    assertFalse(blogRepository.findById(blog.getId()).get().isActive());
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository);
  }

  private void thenEnableBlogByApi(String blogId) {
    webTestClient.put()
        .uri("http://localhost:{port}/api/v1/blogs/enable/{id}", port, blogId)
        .exchange()
        .expectStatus().is2xxSuccessful();
  }

  private void thenDisableBlogByApi(String blogId) {
    webTestClient.put()
        .uri("http://localhost:{port}/api/v1/blogs/disable/{id}", port, blogId)
        .exchange()
        .expectStatus().is2xxSuccessful();
  }


}
