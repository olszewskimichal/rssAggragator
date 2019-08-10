package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.test.StepVerifier;

class BlogActivityUpdaterTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private BlogActivityUpdater blogActivityUpdater;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "blog");
  }

  @Test
  void shouldActivateBlog() {
    //given
    Blog blog = Blog.builder().build();
    blog.deactivate();
    Blog saved = mongoTemplate.save(blog);

    //when
    //then
    StepVerifier.create(blogActivityUpdater.activateBlog(saved.getId()))
        .assertNext(updatedBlog -> assertThat(updatedBlog.isActive()).isTrue())
        .expectComplete()
        .verify();
  }

  @Test
  void shouldDeactivateBlog() {
    //given
    Blog saved = mongoTemplate.save(Blog.builder().build());

    //when
    //then
    StepVerifier.create(blogActivityUpdater.deactivateBlog(saved.getId()))
        .assertNext(updatedBlog -> assertThat(updatedBlog.isActive()).isFalse())
        .expectComplete()
        .verify();
  }
}