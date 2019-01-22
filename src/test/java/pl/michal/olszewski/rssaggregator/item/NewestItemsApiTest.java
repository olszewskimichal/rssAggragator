package pl.michal.olszewski.rssaggregator.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.blog.BlogRepository;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class NewestItemsApiTest extends IntegrationTestBase {

  @Autowired
  private BlogRepository repository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private NewestItemService blogService;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
    itemRepository.deleteAll().block();
    blogService.evictItemsCache();
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

    ListBodySpec<ItemDTO> itemDTOS = thenGetNumberItemsFromApi(3);

    itemDTOS.hasSize(3);
  }

  private ItemListFactory givenItem() {
    return new ItemListFactory(repository, itemRepository);
  }

  private ListBodySpec<ItemDTO> thenGetItemsFromApi() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items", port)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }

  private ListBodySpec<ItemDTO> thenGetNumberItemsFromApi(int number) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items?limit={number}", port, number)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }


}
