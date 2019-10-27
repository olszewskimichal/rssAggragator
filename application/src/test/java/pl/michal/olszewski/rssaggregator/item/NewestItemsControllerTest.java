package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class NewestItemsControllerTest extends IntegrationTestBase {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    itemRepository.deleteAll();
  }

  @Test
  void should_get_empty_list_of_items() {
    givenItem()
        .buildNumberOfItemsAndSave(0, UUID.randomUUID().toString());

    BodySpec<PageItemDTO, ?> result = thenGetItemsFromApi();

    result.value(pageItemDTO -> assertThat(pageItemDTO.getTotalElements()).isEqualTo(0L));
  }

  @Test
  void should_get_all_items() {
    givenItem()
        .buildNumberOfItemsAndSave(3, UUID.randomUUID().toString());

    BodySpec<PageItemDTO, ?> result = thenGetItemsFromApi();

    result.value(pageItemDTO -> {
      assertThat(pageItemDTO.getTotalElements()).isEqualTo(3L);
      assertThat(pageItemDTO.getContent()).hasSize(3);
    });
  }

  @Test
  void should_get_limit_three_items() {
    givenItem()
        .buildNumberOfItemsAndSave(5, UUID.randomUUID().toString());

    BodySpec<PageItemDTO, ?> result = thenGetNumberItemsFromApiWithLimit(3);

    result.value(pageItemDTO -> {
      assertThat(pageItemDTO.getTotalElements()).isEqualTo(5L);
      assertThat(pageItemDTO.getContent()).hasSize(3);
    });
  }

  @Test
  void should_get_second_page_of_items() {
    givenItem()
        .buildNumberOfItemsAndSave(5, UUID.randomUUID().toString());

    BodySpec<PageItemDTO, ?> result = thenGetNumberItemsFromApiWithLimitAndPage(3, 2);

    result.value(pageItemDTO -> {
      assertThat(pageItemDTO.getTotalElements()).isEqualTo(5L);
      assertThat(pageItemDTO.getContent()).hasSize(2);
    });
  }

  @Test
  void should_get_empty_list_of_items_sorted_by_createdAt() {
    givenItem()
        .buildNumberOfItemsAndSave(0, UUID.randomUUID().toString());

    BodySpec<PageItemDTO, ?> result = thenGetItemsSortedByCreatedAtFromApi();

    result.value(pageItemDTO -> {
      assertThat(pageItemDTO.getTotalElements()).isEqualTo(0L);
      assertThat(pageItemDTO.getContent()).hasSize(0);
    });
  }

  @Test
  void should_get_all_items_sorted_by_createdAt() {
    givenItem()
        .buildNumberOfItemsAndSave(3, UUID.randomUUID().toString());

    BodySpec<PageItemDTO, ?> result = thenGetItemsSortedByCreatedAtFromApi();

    result.value(pageItemDTO -> {
      assertThat(pageItemDTO.getTotalElements()).isEqualTo(3L);
      assertThat(pageItemDTO.getContent()).hasSize(3);
    });
  }

  @Test
  void should_get_limit_three_items_sorted_by_createdAt() {
    givenItem()
        .buildNumberOfItemsAndSave(5, UUID.randomUUID().toString());

    BodySpec<PageItemDTO, ?> result = thenGetItemsSortedByCreatedAtWithLimitFromApi(3);

    result.value(pageItemDTO -> {
      assertThat(pageItemDTO.getTotalElements()).isEqualTo(5L);
      assertThat(pageItemDTO.getContent()).hasSize(3);
    });
  }

  @Test
  void should_get_second_page_of_items_sorted_by_createdAt() {
    givenItem()
        .buildNumberOfItemsAndSave(5, UUID.randomUUID().toString());

    BodySpec<PageItemDTO, ?> result = thenGetItemsSortedByCreatedAtForPageAndLimitFromApi(3, 2);

    result.value(pageItemDTO -> {
      assertThat(pageItemDTO.getTotalElements()).isEqualTo(5L);
      assertThat(pageItemDTO.getContent()).hasSize(2);
    });
  }

  private ItemListFactory givenItem() {
    return new ItemListFactory(mongoTemplate);
  }

  private BodySpec<PageItemDTO, ?> thenGetItemsFromApi() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items", port)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageItemDTO.class);
  }

  private BodySpec<PageItemDTO, ?> thenGetNumberItemsFromApiWithLimit(int number) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items?limit={number}", port, number)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageItemDTO.class);
  }

  private BodySpec<PageItemDTO, ?> thenGetNumberItemsFromApiWithLimitAndPage(int number, int page) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items?limit={number}&page={page}", port, number, page)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageItemDTO.class);
  }

  private BodySpec<PageItemDTO, ?> thenGetItemsSortedByCreatedAtFromApi() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items/createdAt", port)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageItemDTO.class);
  }

  private BodySpec<PageItemDTO, ?> thenGetItemsSortedByCreatedAtWithLimitFromApi(int number) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items/createdAt?limit={number}", port, number)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageItemDTO.class);
  }

  private BodySpec<PageItemDTO, ?> thenGetItemsSortedByCreatedAtForPageAndLimitFromApi(int number, int page) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items/createdAt?limit={number}&page={page}", port, number, page)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageItemDTO.class);
  }

}
