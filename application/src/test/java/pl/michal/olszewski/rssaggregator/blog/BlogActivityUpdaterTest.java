package pl.michal.olszewski.rssaggregator.blog;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

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
    Blog blog = new BlogBuilder().build();
    blog.deactivate();
    Blog saved = mongoTemplate.save(blog);

    //when
    //then
    assertTrue(blogActivityUpdater.activateBlog(saved.getId()).isActive());
  }

  @Test
  void shouldDeactivateBlog() {
    //given
    Blog saved = mongoTemplate.save(new BlogBuilder().build());

    //when
    //then
    assertFalse(blogActivityUpdater.deactivateBlog(saved.getId()).isActive());
  }
}