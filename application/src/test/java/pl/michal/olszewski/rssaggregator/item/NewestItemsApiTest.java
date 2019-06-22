package pl.michal.olszewski.rssaggregator.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class NewestItemsApiTest extends IntegrationTestBase {


  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private MongoTemplate entityManager;

  @BeforeEach
  void setUp() {
    itemRepository.deleteAll().block();
  }

  @Test
  void should_get_empty_list_of_items() {
    givenItem()
        .buildNumberOfItemsAndSave(0);

    ListBodySpec<ItemDTO> items = thenGetItemsFromApi();

    items.hasSize(0);
  }

  @Test
  void should_get_all_items() {
    givenItem()
        .buildNumberOfItemsAndSave(3);

    ListBodySpec<ItemDTO> items = thenGetItemsFromApi();

    items.hasSize(3);
  }

  @Test
  void should_get_limit_three_items() {
    givenItem()
        .buildNumberOfItemsAndSave(6);

    ListBodySpec<ItemDTO> itemDTOS = thenGetNumberItemsFromApiWithLimit(3);

    itemDTOS.hasSize(3);
  }

  @Test
  void should_get_second_page_of_items() {
    givenItem()
        .buildNumberOfItemsAndSave(6);

    ListBodySpec<ItemDTO> itemDTOS = thenGetNumberItemsFromApiWithLimitAndPage(3, 2);

    itemDTOS.hasSize(3);
  }

  @Test
  void should_get_empty_list_of_items_sorted_by_createdAt() {
    givenItem()
        .buildNumberOfItemsAndSave(0);

    ListBodySpec<ItemDTO> items = thenGetItemsSortedByCreatedAtFromApi();

    items.hasSize(0);
  }

  @Test
  void should_get_all_items_sorted_by_createdAt() {
    givenItem()
        .buildNumberOfItemsAndSave(3);

    ListBodySpec<ItemDTO> items = thenGetItemsSortedByCreatedAtFromApi();

    items.hasSize(3);
  }

  @Test
  void should_get_limit_three_items_sorted_by_createdAt() {
    givenItem()
        .buildNumberOfItemsAndSave(6);

    ListBodySpec<ItemDTO> itemDTOS = thenGetItemsSortedByCreatedAtWithLimitFromApi(3);

    itemDTOS.hasSize(3);
  }

  @Test
  void should_get_second_page_of_items_sorted_by_createdAt() {
    givenItem()
        .buildNumberOfItemsAndSave(6);

    ListBodySpec<ItemDTO> itemDTOS = thenGetItemsSortedByCreatedAtForPageAndLimitFromApi(3, 2);

    itemDTOS.hasSize(3);
  }

  private ItemListFactory givenItem() {
    return new ItemListFactory(entityManager);
  }

  private ListBodySpec<ItemDTO> thenGetItemsFromApi() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items", port)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }

  private ListBodySpec<ItemDTO> thenGetNumberItemsFromApiWithLimit(int number) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items?limit={number}", port, number)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }

  private ListBodySpec<ItemDTO> thenGetNumberItemsFromApiWithLimitAndPage(int number, int page) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items?limit={number}&page={page}", port, number, page)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }

  private ListBodySpec<ItemDTO> thenGetItemsSortedByCreatedAtFromApi() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items/createdAt", port)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }

  private ListBodySpec<ItemDTO> thenGetItemsSortedByCreatedAtWithLimitFromApi(int number) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items/createdAt?limit={number}", port, number)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }

  private ListBodySpec<ItemDTO> thenGetItemsSortedByCreatedAtForPageAndLimitFromApi(int number, int page) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items/createdAt?limit={number}&page={page}", port, number, page)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }

}
