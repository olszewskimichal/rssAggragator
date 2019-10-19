package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class ReadItemApiIntegrationTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ItemRepository itemRepository;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
  }

  @Test
  void should_mark_item_as_read() {
    Item item = itemRepository.save(new Item(new ItemDTOBuilder().link("item").build()));

    thenMarkItemAsRead(item.getId());

    Optional<Item> itemRepositoryById = itemRepository.findById(item.getId());
    assertThat(itemRepositoryById).isPresent();
    assertTrue(itemRepositoryById.get().isRead());
  }

  @Test
  void should_mark_item_as_unread() {
    Item item = new Item(new ItemDTOBuilder().link("link").build());
    itemRepository.save(item.markAsRead());

    thenMarkItemAsUnread(item.getId());

    Optional<Item> itemRepositoryById = itemRepository.findById(item.getId());
    assertThat(itemRepositoryById).isPresent();
    assertFalse(itemRepositoryById.get().isRead());
  }


  private void thenMarkItemAsRead(String itemID) {
    var itemDTO = new ReadItemDTO(itemID, true);
    post(itemDTO);
  }

  private void thenMarkItemAsUnread(String itemID) {
    var itemDTO = new ReadItemDTO(itemID, false);
    post(itemDTO);
  }

  private void post(ReadItemDTO itemDTO) {
    webTestClient.post()
        .uri("http://localhost:{port}/api/v1/items/mark", port)
        .body(fromObject(itemDTO))
        .exchange()
        .expectStatus().isNoContent();
  }

}
