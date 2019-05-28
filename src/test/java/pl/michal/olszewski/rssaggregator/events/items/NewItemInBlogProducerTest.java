package pl.michal.olszewski.rssaggregator.events.items;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class NewItemInBlogProducerTest extends IntegrationTestBase {

  @Autowired
  private NewItemInBlogEventProducer eventProducer;

  @Autowired
  private NewItemInBlogEventRepository repository;

  @Test
  void shouldPersistNewEventToDbWhenWriteEventToQueue() throws InterruptedException {
    //given
    eventProducer.writeEventToQueue(new NewItemInBlogEvent(Instant.now(), "link", "title", "id"));
    //when
    Thread.sleep(100);
    //then
    assertThat(repository.count()).isEqualTo(1L);
  }
}