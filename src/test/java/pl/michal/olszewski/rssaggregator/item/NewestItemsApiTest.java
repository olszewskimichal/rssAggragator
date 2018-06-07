package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.blog.BlogRepository;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class NewestItemsApiTest extends IntegrationTestBase {

  @Autowired
  private BlogRepository repository;

  @Autowired
  private NewestItemService blogService;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
    blogService.evictItemsCache();
  }

  @Test
  void should_get_empty_list_of_items() {
    givenItem()
        .buildNumberOfItemsAndSave(0);

    List<ItemDTO> items = thenGetItemsFromApi();

    assertThat(items).isEmpty();
  }

  @Test
  void should_get_all_items() {
    givenItem()
        .buildNumberOfItemsAndSave(3);

    List<ItemDTO> items = thenGetItemsFromApi();

    assertThat(items).hasSize(3);
  }

  @Test
  void should_get_limit_three_items() {
    givenItem()
        .buildNumberOfItemsAndSave(6);

    List<ItemDTO> itemDTOS = thenGetNumberItemsFromApi(3);

    ItemListAssert.assertThat(itemDTOS)
        .isSuccessful()
        .hasNumberOfItems(3);
  }

  private ItemListFactory givenItem() {
    return new ItemListFactory(repository);
  }

  private List<ItemDTO> thenGetItemsFromApi() {
    return Arrays.asList(Objects.requireNonNull(template.getForEntity(String.format("http://localhost:%s/api/v1/items", port), ItemDTO[].class).getBody()));
  }

  private List<ItemDTO> thenGetNumberItemsFromApi(int number) {
    return Arrays.asList(Objects.requireNonNull(template.getForEntity(String.format("http://localhost:%s/api/v1/items?limit=%s", port, number), ItemDTO[].class).getBody()));
  }


}
