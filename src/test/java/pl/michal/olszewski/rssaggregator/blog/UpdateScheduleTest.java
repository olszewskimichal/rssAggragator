package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.events.EventRepository;
import pl.michal.olszewski.rssaggregator.extenstions.TimeExecutionLogger;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class UpdateScheduleTest extends IntegrationTestBase implements TimeExecutionLogger {

  @Autowired
  private UpdateBlogService updateBlogService;

  @Autowired
  private BlogReactiveRepository blogRepository;

  @Autowired
  private BlogService blogService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private EventRepository eventRepository;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
    blogRepository.deleteAll().block();
    eventRepository.deleteAll().block();
    blogService.evictBlogCache();
  }

  @Test
  void shouldUpdateBlog() {
    Blog blog = Blog.builder()
        .blogURL("https://spring.io/")
        .name("spring")
        .feedURL("https://spring.io/blog.atom/")
        .build();
    blogRepository.save(blog).block();

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(true)
        .expectComplete()
        .verify();

    Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());
    StepVerifier
        .create(updatedBlog)
        .assertNext(v -> assertThat(v.getItems()).isNotNull().isNotEmpty())
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
    blogRepository.save(blog).block();

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(true)
        .expectComplete()
        .verify();

    Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());
    StepVerifier
        .create(updatedBlog)
        .assertNext(v -> assertThat(v.getItems()).hasSize(0))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldUpdateBlogWithNotValidCertification() {
    Blog blog = Blog.builder()
        .blogURL("https://koziolekweb.pl")
        .name("koziolekweb.pl")
        .feedURL("https://koziolekweb.pl/feed/")
        .lastUpdateDate(Instant.now())
        .build();
    blogRepository.save(blog).block();

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(true)
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

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(false)
        .verifyComplete();

    StepVerifier.create(eventRepository.count())
        .expectNext(1L)
        .verifyComplete();
  }

  @Test
  void shouldReturnFalseOnTimeoutAndWriteNewEventToDB() {
    Blog blog = Blog.builder()
        .blogURL("https://spring.io/")
        .name("spring")
        .feedURL("https://spring.io/blog.atom/")
        .build();
    blogRepository.save(blog).block();

    Mono<Boolean> result = updateBlogService.updateRssBlogItems(blog);
    StepVerifier.withVirtualTime(() -> result)
        .thenAwait(Duration.ofSeconds(5))
        .expectNext(false)
        .verifyComplete();
    StepVerifier.create(eventRepository.count())
        .expectNext(1L)
        .verifyComplete();
  }
}
