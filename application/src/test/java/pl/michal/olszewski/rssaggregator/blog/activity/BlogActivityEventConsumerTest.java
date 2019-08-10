package pl.michal.olszewski.rssaggregator.blog.activity;

import static java.time.Instant.now;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static pl.michal.olszewski.rssaggregator.blog.BlogDTO.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogFinder;
import pl.michal.olszewski.rssaggregator.blog.BlogUpdater;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.test.StepVerifier;

class BlogActivityEventConsumerTest extends IntegrationTestBase {

  @Autowired
  private BlogActivityEventConsumer eventConsumer;

  @Autowired
  private BlogFinder repository;

  @Autowired
  private BlogUpdater updater;


  @BeforeEach
  void setUp() {
    updater.deleteAll().block();
  }

  @Test
  void shouldActivateBlogByEvent() {
    Blog blog = updater.createNewBlog(builder().build()).block();
    eventConsumer.receiveActivateMessage(ActivateBlog.builder().occurredAt(now()).blogId(blog.getId()).build());
    //then
    StepVerifier.create(repository.findById(blog.getId()))
        .assertNext(blogFromDB -> assertThat(blogFromDB.isActive()).isTrue())
        .expectComplete()
        .verify();
  }

  @Test
  void shouldDeactivateBlogByEvent() {
    Blog blog = updater.createNewBlog(builder().build()).block();
    eventConsumer.receiveDeactivateMessage(DeactivateBlog.builder().occurredAt(now()).blogId(blog.getId()).build());
    //when
    //then
    StepVerifier.create(repository.findById(blog.getId()))
        .assertNext(blogFromDb -> assertThat(blogFromDb.isActive()).isFalse())
        .expectComplete()
        .verify();
  }
}