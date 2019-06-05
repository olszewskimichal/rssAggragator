package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.test.StepVerifier;

class BlogActivityEventProducerTest extends IntegrationTestBase {

  @Autowired
  private BlogActivityEventProducer eventProducer;

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
  void shouldActivateBlogByEvent() throws InterruptedException {
    Blog blog = repository.save(Blog.builder().build()).block();
    eventProducer.writeEventToQueue(ActivateBlog.builder().occurredAt(Instant.now()).blogId(blog.getId()).build());
    //when
    Thread.sleep(100);
    //then
    StepVerifier.create(repository.findById(blog.getId()))
        .assertNext(v -> assertThat(v.isActive()).isTrue())
        .expectComplete()
        .verify();
    StepVerifier.create(activityBlogEventRepository.count())
        .expectNext(1L)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldDeactivateBlogByEvent() throws InterruptedException {
    Blog blog = repository.save(Blog.builder().build()).block();
    eventProducer.writeEventToQueue(DeactivateBlog.builder().occurredAt(Instant.now()).blogId(blog.getId()).build());
    //when
    Thread.sleep(100);
    //then
    StepVerifier.create(repository.findById(blog.getId()))
        .assertNext(v -> assertThat(v.isActive()).isFalse())
        .expectComplete()
        .verify();
    StepVerifier.create(activityBlogEventRepository.count())
        .expectNext(1L)
        .expectComplete()
        .verify();
  }
}