package pl.michal.olszewski.rssaggregator.blog.search.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.Item;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

class ItemSearchControllerIntegrationTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
  }

  @Test
  void shouldReturnSearchResultByMatchingTextWithoutLimit() {
    mongoTemplate.save(new Item(ItemDTO.builder().link("link1").title("AAA").build()));
    mongoTemplate.save(new Item(ItemDTO.builder().link("link2").title("BBB").build()));
    mongoTemplate.save(new Item(ItemDTO.builder().link("link3").title("CCC").build()));

    ListBodySpec<ItemSearchResult> result = thenGetSearchResultFromAPI("AAA", null);

    result.hasSize(1);
  }

  @Test
  void shouldReturnSearchResultByMatchingTextWithLimit() {
    mongoTemplate.save(new Item(ItemDTO.builder().link("link1").title("BBB").build()));
    mongoTemplate.save(new Item(ItemDTO.builder().link("link2").title("BBB").build()));
    mongoTemplate.save(new Item(ItemDTO.builder().link("link3").title("CCC").build()));

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