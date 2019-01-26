package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.UnknownHostException;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.extenstions.TimeExecutionLogger;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class UpdateScheduleTest extends IntegrationTestBase implements TimeExecutionLogger {

  @Autowired
  private AsyncService asyncService;
  @Autowired
  private BlogReactiveRepository blogRepository;

  @Autowired
  private BlogService blogService;

  @BeforeEach
  void setUp() {
    blogService.evictBlogCache();
  }

  //TODO wyprostowac metode
  @Test
  void shouldUpdateBlog() {
    blogRepository.deleteAll().block();
    Blog blog = new Blog("https://devstyle.pl", "devstyle.pl", "devstyle.pl", "https://devstyle.pl/feed", null, null); //TODO krotsza linia
    blogRepository.save(blog).block();
    Boolean voidFuture = asyncService.updateBlog(blog);
    Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());

    StepVerifier
        .create(updatedBlog)
        .assertNext(v -> assertThat(v.getItems()).isNotEmpty().hasSize(15))
        .expectComplete()
        .verify();
  }

  //TODO wyprostowac metode
  @Test
  void shouldNotUpdateBlogWhenLastUpdatedDateIsAfterPublishedItems() {
    blogRepository.deleteAll().block();
    Blog blog = blogRepository.save(new Blog("https://devstyle.pl", "devstyle.pl", "devstyle.pl", "https://devstyle.pl/feed", null, Instant.now())).block(); //TODO krotsza linia bez blocka
    Boolean voidFuture = asyncService.updateBlog(blog);
    Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());

    StepVerifier
        .create(updatedBlog)
        .assertNext(v -> assertThat(v.getLastUpdateDate()).isNotNull().isBefore(Instant.now()))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldNotUpdateBlog() {
    Blog blog = new Blog("https://devstyle.xxx", "DEVSTYLE", "devstyle", "https://devstyle.xxx/feed", null, null); //TODO krotsza linia
    blogRepository.save(blog).block();
    assertThatThrownBy(() -> asyncService.updateBlog(blog))
        .isInstanceOf(RssException.class)
        .hasCauseInstanceOf(UnknownHostException.class);
  }
}
