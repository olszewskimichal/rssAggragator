package pl.michal.olszewski.rssaggregator.events.items;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class NewItemInBlogConsumerTest extends IntegrationTestBase {

  @Autowired
  private NewItemInBlogEventConsumer eventConsumer;

  @Autowired
  private NewItemInBlogEventRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @Test
  void shouldPersistNewEventToDbOnEvent() {
    //given
    eventConsumer.receiveMessage(new NewItemInBlogEvent(Instant.now(), "link", "title", "id"));
    //then
    assertThat(repository.count()).isEqualTo(1L);
  }
}