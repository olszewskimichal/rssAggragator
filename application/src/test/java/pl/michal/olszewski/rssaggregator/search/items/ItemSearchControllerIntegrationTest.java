package pl.michal.olszewski.rssaggregator.search.items;

import java.io.IOException;
import java.util.stream.Stream;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.ElasticIntegrationTestBase;
import reactor.core.publisher.Mono;

class ItemSearchControllerIntegrationTest extends ElasticIntegrationTestBase {

  @Autowired
  private ReactiveElasticsearchOperations elasticsearchOperations;

  @BeforeEach
  void setUp() throws IOException, InterruptedException {
    setupElastic();
    NativeSearchQuery query = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.matchAllQuery())
        .build();
    elasticsearchOperations.deleteBy(query, ItemForSearch.class).block();
  }

  @Test
  void shouldReturnSearchResultByMatchingTextWithoutLimit() {
    Stream.of(
        new ItemForSearchBuilder().link("link1").title("AAA").build(),
        new ItemForSearchBuilder().link("link2").title("BBB").build(),
        new ItemForSearchBuilder().link("link3").title("CCC").build()
    ).map(itemForSearch -> elasticsearchOperations.save(itemForSearch))
        .forEach(Mono::block);

    ListBodySpec<ItemSearchResult> result = thenGetSearchResultFromAPI("AAA", null);

    result.hasSize(1);
  }

  @Test
  void shouldReturnSearchResultByMatchingTextWithLimit() {
    Stream.of(
        new ItemForSearchBuilder().link("link1").title("BBB").build(),
        new ItemForSearchBuilder().link("link2").title("BBB").build(),
        new ItemForSearchBuilder().link("link3").title("CCC").build()
    ).map(itemForSearch -> elasticsearchOperations.save(itemForSearch))
        .forEach(Mono::block);

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