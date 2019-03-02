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
    blogRepository.deleteAll().block();
    blogService.evictBlogCache();
  }

  @Test
  void shouldUpdateBlog() {
    Blog blog = Blog.builder()
        .blogURL("https://devstyle.pl")
        .name("devstyle.pl")
        .feedURL("https://devstyle.pl/feed")
        .build();
    blogRepository.save(blog).block();

    Boolean voidFuture = asyncService.updateBlog(blog, "correlationId");

    Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());
    StepVerifier
        .create(updatedBlog)
        .assertNext(v -> assertThat(v.getItems()).isNotEmpty().hasSize(15))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldNotUpdateBlogWhenLastUpdatedDateIsAfterPublishedItems() {

    Blog blog = Blog.builder()
        .blogURL("https://devstyle.pl")
        .name("devstyle.pl")
        .feedURL("https://devstyle.pl/feed")
        .lastUpdateDate(Instant.now())
        .build();

    Boolean voidFuture = asyncService.updateBlog(blog, "correlationId");

    Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());
    StepVerifier
        .create(updatedBlog)
        .assertNext(v -> assertThat(v.getLastUpdateDate()).isNotNull().isBefore(Instant.now()))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldNotUpdateBlog() {
    Blog blog = Blog.builder()
        .blogURL("https://devstyle.pl")
        .name("devstyle.pl")
        .feedURL("https://devstyle.xxx/feed")
        .build();

    blogRepository.save(blog).block();

    assertThatThrownBy(() -> asyncService.updateBlog(blog, "correlationId"))
        .isInstanceOf(RssException.class)
        .hasCauseInstanceOf(UnknownHostException.class);
  }
}
