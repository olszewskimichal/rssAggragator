package pl.michal.olszewski.rssaggregator.blog.activity;

import static java.time.Instant.now;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static pl.michal.olszewski.rssaggregator.blog.Blog.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.test.StepVerifier;

class BlogActivityEventConsumerTest extends IntegrationTestBase {

  @Autowired
  private BlogActivityEventConsumer eventConsumer;

  @Autowired
  private BlogReactiveRepository repository;

  @Autowired
  private ChangeActivityBlogEventRepository activityBlogEventRepository;

  @BeforeEach
  void setUp() {
    repository.deleteAll().block();
    activityBlogEventRepository.deleteAll().block();
  }

  @Test
  void shouldActivateBlogByEvent() {
    Blog blog = repository.save(builder().build()).block();
    eventConsumer.receiveActivateMessage(ActivateBlog.builder().occurredAt(now()).blogId(blog.getId()).build());
    //then
    StepVerifier.create(repository.findById(blog.getId()))
        .assertNext(blogFromDB -> assertThat(blogFromDB.isActive()).isTrue())
        .expectComplete()
        .verify();
    StepVerifier.create(activityBlogEventRepository.count())
        .expectNext(1L)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldDeactivateBlogByEvent() {
    Blog blog = repository.save(builder().build()).block();
    eventConsumer.receiveDeactivateMessage(DeactivateBlog.builder().occurredAt(now()).blogId(blog.getId()).build());
    //when
    //then
    StepVerifier.create(repository.findById(blog.getId()))
        .assertNext(blogFromDb -> assertThat(blogFromDb.isActive()).isFalse())
        .expectComplete()
        .verify();
    StepVerifier.create(activityBlogEventRepository.count())
        .expectNext(1L)
        .expectComplete()
        .verify();
  }
}