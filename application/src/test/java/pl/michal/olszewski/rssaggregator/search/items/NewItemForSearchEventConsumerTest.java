package pl.michal.olszewski.rssaggregator.search.items;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import pl.michal.olszewski.rssaggregator.integration.ElasticIntegrationTestBase;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;
import reactor.test.StepVerifier;

class NewItemForSearchEventConsumerTest extends ElasticIntegrationTestBase {

  @Autowired
  private NewItemForSearchEventConsumer eventConsumer;

  @Autowired
  private ReactiveElasticsearchOperations reactiveElasticsearchTemplate;

  @BeforeEach
  void setUp() throws IOException, InterruptedException {
    setupElastic();
    NativeSearchQuery deleteQuery = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.matchAllQuery())
        .build();
    reactiveElasticsearchTemplate.deleteBy(deleteQuery, ItemForSearch.class).block();
  }

  @Test
  void shouldPersistNewEventToDbOnEvent() {
    //given
    NewItemForSearchEvent event = NewItemForSearchEvent.builder()
        .linkUrl("link")
        .itemTitle("title")
        .itemDescription("desc")
        .build();
    eventConsumer.receiveMessage(event);
    //when
    StepVerifier.create(reactiveElasticsearchTemplate.count(ItemForSearch.class))
        .assertNext(count -> assertThat(count).isEqualTo(1L))
        .expectComplete()
        .verify();
  }
}
