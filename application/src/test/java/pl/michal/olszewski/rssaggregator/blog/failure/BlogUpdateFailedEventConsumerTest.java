package pl.michal.olszewski.rssaggregator.blog.failure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class BlogUpdateFailedEventConsumerTest extends IntegrationTestBase {

  @Autowired
  private BlogUpdateFailedEventConsumer eventConsumer;

  @Autowired
  private BlogUpdateFailedEventRepository blogUpdateFailedEventRepository;

  @BeforeEach
  void setUp() {
    blogUpdateFailedEventRepository.deleteAll();
  }

  @Test
  void shouldPersistNewEventToDbOnEvent() {
    //given
    eventConsumer.receiveMessage(BlogUpdateFailedEvent.builder().occurredAt(Instant.now()).blogId("id").build());
    //when
    assertThat(blogUpdateFailedEventRepository.count()).isEqualTo(1L);
  }
}