package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.ParallelFlux;
import reactor.test.StepVerifier;

class ItemsDescriptionHtmlTagRemovalWorkerTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;


  @Autowired
  private ItemsDescriptionHtmlTagRemovalWorker worker;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
  }

  @Test
  void shouldUpdateDescriptionForItem() {
    //given
    Item item = new Item(ItemDTO.builder()
        .link("title")
        .title("title")
        .description(
            "<p>test <a rel=\"nofollow\" href=\"http://testest.pl\">test</a>.</p>")
        .date(Instant.now())
        .build());
    mongoTemplate.save(item);
    Item item2 = new Item(ItemDTO.builder()
        .link("title2")
        .title("title2")
        .description(
            "<p>test <a rel=\"nofollow\" href=\"http://testest.pl\">test</a>.</p>")
        .date(Instant.now())
        .build());
    mongoTemplate.save(item2);
    //when
    ParallelFlux<Item> items = worker.processAllItemsAndRemoveHtmlTagsFromDescription();
    //then
    StepVerifier.create(items)
        .assertNext(v -> assertThat(v.getDescription()).isEqualTo("test test."))
        .assertNext(v -> assertThat(v.getDescription()).isEqualTo("test test."))
        .expectComplete()
        .verify();
  }
}
