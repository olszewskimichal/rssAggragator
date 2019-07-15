package pl.michal.olszewski.rssaggregator.blog.failure;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class FailureUpdatesBlogControllerTest extends IntegrationTestBase {

  @Autowired
  private BlogUpdateFailedEventRepository failedEventRepository;

  @BeforeEach
  void setUp() {
    failedEventRepository.deleteAll();
  }

  @Test
  void shouldReturnAllFailureCountByAPI() {
    List<BlogUpdateFailedEvent> eventList = List.of(
        new BlogUpdateFailedEvent(Instant.now(), "id", "blogURL", "blogId", "error"),
        new BlogUpdateFailedEvent(Instant.now(), "id", "blogURL", "blogId", "error"),
        new BlogUpdateFailedEvent(Instant.now(), "id", "blogURL", "blogId", "error"),
        new BlogUpdateFailedEvent(Instant.now(), "id", "blogURL2", "blogId2", "error"),
        new BlogUpdateFailedEvent(Instant.now(), "id", "blogURL2", "blogId2", "error")
    );

    failedEventRepository.saveAll(eventList);

    ListBodySpec<UpdateBlogFailureCount> result = thenGetAllFailureCountByAPI();
    result.hasSize(2);
  }

  @Test
  void shouldReturnFailureFromLast24h() {
    List<BlogUpdateFailedEvent> eventList = List.of(
        new BlogUpdateFailedEvent(Instant.now().minus(2, ChronoUnit.DAYS), "id", "blogURL", "blogId", "error"),
        new BlogUpdateFailedEvent(Instant.now(), "id", "blogURL", "blogId", "error"),
        new BlogUpdateFailedEvent(Instant.now().minus(12, ChronoUnit.HOURS), "id", "blogURL", "blogId", "error"),
        new BlogUpdateFailedEvent(Instant.now().minus(1, ChronoUnit.DAYS), "id", "blogURL2", "blogId2", "error"),
        new BlogUpdateFailedEvent(Instant.now(), "id", "blogURL2", "blogId2", "error")
    );

    failedEventRepository.saveAll(eventList);

    ListBodySpec<UpdateBlogFailureCount> result = thenGetFailureCountFromLast24ByAPI();
    result.hasSize(1);
  }

  private ListBodySpec<UpdateBlogFailureCount> thenGetAllFailureCountByAPI() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/failure", port)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(UpdateBlogFailureCount.class);
  }

  private ListBodySpec<UpdateBlogFailureCount> thenGetFailureCountFromLast24ByAPI() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/failure/lastDay", port)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(UpdateBlogFailureCount.class);
  }
}