package pl.michal.olszewski.rssaggregator.search.items;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.michal.olszewski.rssaggregator.search.items.ItemForSearch.builder;

import java.util.stream.Stream;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import pl.michal.olszewski.rssaggregator.integration.ElasticIntegrationTestBase;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ItemTextSearchRepositoryImplTest extends ElasticIntegrationTestBase {

  @Autowired
  private ReactiveElasticsearchOperations elasticsearchOperations;

  @Autowired
  private ItemTextSearchRepository itemTextSearchRepository;

  @BeforeEach
  void setUp() {
    NativeSearchQuery query = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.matchAllQuery())
        .build();
    elasticsearchOperations.deleteBy(query, ItemForSearch.class).block();
  }

  @Test
  void shouldFindItemWhereTitleMatchingToQuery() {
    //given
    Stream.of(
        builder().link("URL1").title("TDD in JAVA").description("TDD").build(),
        builder().link("URL2").title("TDD in PYTHON").description("TDD").build(),
        builder().link("URL3").title("TDD in JAVASCRIPT").description("TDD").build()
    ).map(itemForSearch -> elasticsearchOperations.save(itemForSearch))
        .forEach(Mono::block);
    //when
    StepVerifier.create(itemTextSearchRepository.findMatching("Java", 2))
        .assertNext(searchResult -> assertThat(searchResult.getTitle()).isEqualTo("TDD in JAVA"))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldFindItemWhereDescriptionMatchingToQuery() {
    //given
    Stream.of(
        builder().link("URL1").description("TDD in JAVA").title("TDD").build(),
        builder().link("URL2").description("TDD in PYTHON").title("TDD").build(),
        builder().link("URL3").description("TDD in JAVASCRIPT").title("TDD").build()
    ).map(itemForSearch -> elasticsearchOperations.save(itemForSearch))
        .forEach(Mono::block);

    //when
    StepVerifier.create(itemTextSearchRepository.findMatching("Java", 2))
        .assertNext(searchResult -> assertThat(searchResult.getDescription()).isEqualTo("TDD in JAVA"))
        .expectComplete()
        .verify();
  }
}