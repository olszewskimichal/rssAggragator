package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.test.StepVerifier;

class BlogActivityUpdaterTest extends IntegrationTestBase {

  @Autowired
  private BlogReactiveRepository reactiveRepository;

  @Autowired
  private BlogActivityUpdater blogActivityUpdater;

  @BeforeEach
  void setUp() {
    reactiveRepository.deleteAll().block();
  }

  @Test
  void shouldActivateBlog() {
    //given
    Blog blog = Blog.builder().build();
    blog.deactivate();
    Blog saved = reactiveRepository.save(blog).block();

    //when
    //then
    StepVerifier.create(blogActivityUpdater.activateBlog(saved.getId()))
        .assertNext(v -> assertThat(v.isActive()).isTrue())
        .expectComplete()
        .verify();
  }

  @Test
  void shouldDeactivateBlog() {
    //given
    Blog saved = reactiveRepository.save(Blog.builder().build()).block();

    //when
    //then
    StepVerifier.create(blogActivityUpdater.deactivateBlog(saved.getId()))
        .assertNext(v -> assertThat(v.isActive()).isFalse())
        .expectComplete()
        .verify();
  }
}