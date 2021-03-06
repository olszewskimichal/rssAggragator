package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class NewItemInBlogConsumerTest extends IntegrationTestBase {

  @Autowired
  private NewItemInBlogEventConsumer eventConsumer;

  @Autowired
  private ItemRepository itemRepository;

  @BeforeEach
  void setUp() {
    itemRepository.deleteAll();
  }

  @Test
  void shouldPersistNewEventAndNewItemToDbOnEvent() {
    //given
    ItemDTO item = new ItemDTOBuilder().link("link")
        .title("title")
        .blogId("id")
        .build();
    eventConsumer.receiveMessage(new NewItemInBlogEvent(item));
    //then
    assertThat(itemRepository.count()).isEqualTo(1L);
  }
}