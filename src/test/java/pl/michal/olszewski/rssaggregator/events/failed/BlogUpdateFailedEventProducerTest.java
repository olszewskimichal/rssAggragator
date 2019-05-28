package pl.michal.olszewski.rssaggregator.events.failed;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class BlogUpdateFailedEventProducerTest extends IntegrationTestBase {

  @Autowired
  private BlogUpdateFailedEventProducer eventProducer;

  @Autowired
  private BlogUpdateFailedEventRepository blogUpdateFailedEventRepository;

  @BeforeEach
  void setUp() {
    blogUpdateFailedEventRepository.deleteAll();
  }

  @Test
  void shouldPersistNewEventToDbWhenWriteEventToQueue() throws InterruptedException {
    //given
    eventProducer.writeEventToQueue(new BlogUpdateFailedEvent(Instant.now(), "id", "url", "id", "msg", "trace"));
    //when
    Thread.sleep(100);
    //then
    assertThat(blogUpdateFailedEventRepository.count()).isEqualTo(1L);
  }
}