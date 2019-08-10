package pl.michal.olszewski.rssaggregator.search.items;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;

class NewItemForSearchEventConsumerTest extends IntegrationTestBase {

  @Autowired
  private NewItemForSearchEventConsumer eventConsumer;

  @Autowired
  private ItemForSearchRepository itemForSearchRepository;

  @BeforeEach
  void setUp() {
    itemForSearchRepository.deleteAll();
  }

  @Test
  void shouldPersistNewEventToDbOnEvent() {
    //given
    NewItemForSearchEvent event = NewItemForSearchEvent.builder()
        .linkUrl("link")
        .itemTitle("title")
        .itemDescription("desc")
        .build();
    eventConsumer.receiveMessage(event);
    //when
    assertThat(itemForSearchRepository.count()).isEqualTo(1L);
  }
}
