package pl.michal.olszewski.rssaggregator.blog.failure;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class BlogUpdateFailedEventAggregatorTest extends IntegrationTestBase {

  @Autowired
  private BlogUpdateFailedEventRepository eventRepository;

  @Autowired
  private BlogUpdateFailedEventAggregator aggregator;

  @BeforeEach
  void setUp() {
    eventRepository.deleteAll();
  }

  @Test
  void shouldAggregateEventsByBlogId() {
    //given
    List<BlogUpdateFailedEvent> events = Arrays.asList(
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("msg1").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("msg1").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("msg1").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("msg1").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("msg2").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("msg1").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("msg1").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("msg2").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("msg2").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("msg2").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id3").errorMsg("msg1").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id4").errorMsg("msg1").occurredAt(Instant.now()).build()
    );
    eventRepository.saveAll(events);
    //when
    Flux<UpdateBlogFailureCount> failureCounts = aggregator.aggregateAllFailureOfBlogs();

    StepVerifier.create(failureCounts)
        .expectNext(new UpdateBlogFailureCount("id1", "msg1", 4L))
        .expectNext(new UpdateBlogFailureCount("id2", "msg2", 3L))
        .expectNext(new UpdateBlogFailureCount("id2", "msg1", 2L))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldAggregateEventsByBlogIdForLast24hours() {
    //given
    List<BlogUpdateFailedEvent> eventList = Arrays.asList(
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("error").occurredAt(Instant.now().minus(2, ChronoUnit.DAYS)).build(),
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("error").occurredAt(Instant.now().minus(2, ChronoUnit.DAYS)).build(),
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("error").occurredAt(Instant.now().minus(23, ChronoUnit.HOURS)).build(),
        BlogUpdateFailedEvent.builder().blogId("id1").errorMsg("error").occurredAt(Instant.now().minus(23, ChronoUnit.HOURS)).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("error").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("error").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("error").occurredAt(Instant.now()).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("error").occurredAt(Instant.now().plusSeconds(100)).build(),
        BlogUpdateFailedEvent.builder().blogId("id2").errorMsg("error").occurredAt(Instant.now().plusSeconds(100)).build()
    );
    eventRepository.saveAll(eventList);

    //when
    Flux<UpdateBlogFailureCount> failureCounts = aggregator.aggregateAllFailureOfBlogsFromPrevious24h();

    StepVerifier.create(failureCounts)
        .expectNext(new UpdateBlogFailureCount("id2", "error", 3L))
        .expectNext(new UpdateBlogFailureCount("id1", "error", 2L))
        .expectComplete()
        .verify();
  }

}