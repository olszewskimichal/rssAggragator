package pl.michal.olszewski.rssaggregator.search.items;

import static pl.michal.olszewski.rssaggregator.search.items.ItemForSearch.builder;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class ItemSearchControllerIntegrationTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "itemsSearch");
  }

  @Test
  void shouldReturnSearchResultByMatchingTextWithoutLimit() {
    List<ItemForSearch> itemList = List.of(
        builder().link("link1").title("AAA").build(),
        builder().link("link2").title("BBB").build(),
        builder().link("link3").title("CCC").build()
    );
    mongoTemplate.insertAll(itemList);

    ListBodySpec<ItemSearchResult> result = thenGetSearchResultFromAPI("AAA", null);

    result.hasSize(1);
  }

  @Test
  void shouldReturnSearchResultByMatchingTextWithLimit() {
    List<ItemForSearch> itemList = List.of(
        builder().link("link1").title("BBB").build(),
        builder().link("link2").title("BBB").build(),
        builder().link("link3").title("CCC").build()
    );
    mongoTemplate.insertAll(itemList);

    ListBodySpec<ItemSearchResult> result = thenGetSearchResultFromAPI("BBB", 1);

    result.hasSize(1);
  }

  private ListBodySpec<ItemSearchResult> thenGetSearchResultFromAPI(String text, Integer limit) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/items/search?text={text}&limit={limit}", port, text, limit)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemSearchResult.class);
  }

}