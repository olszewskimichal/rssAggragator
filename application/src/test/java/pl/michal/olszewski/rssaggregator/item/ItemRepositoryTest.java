package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mongodb.MongoWriteException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
@EnableMongoAuditing
class ItemRepositoryTest {

  @Autowired
  protected MongoTemplate mongoTemplate;

  @Autowired
  private ItemRepository itemRepository;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
  }

  @Test
  void shouldFind2NewestPublishedItems() {
    //given
    Instant instant = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    Item newestItem = new Item(new ItemDTOBuilder().link("newestItem").date(instant.plusSeconds(10)).build());
    Item presentItem = new Item(new ItemDTOBuilder().link("presentItem").date(instant).build());

    mongoTemplate.insertAll(Arrays.asList(
        presentItem,
        newestItem,
        new Item(new ItemDTOBuilder().link("title2").date(instant.minusSeconds(10)).build())
    ));
    //when
    Flux<Item> items = itemRepository.findAllOrderByPublishedDate(2, 0);

    //then
    StepVerifier.create(items)
        .expectNext(newestItem)
        .expectNext(presentItem)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldFind2NewestPersistedItems() {
    //given
    mongoTemplate.save(new Item(new ItemDTOBuilder().link("title2").build()));
    Item title1 = mongoTemplate.save(new Item(new ItemDTOBuilder().link("title1").build()));
    Item title3 = mongoTemplate.save(new Item(new ItemDTOBuilder().link("title3").build()));

    //when
    Flux<Item> items = itemRepository.findAllOrderByCreatedAt(2, 0);

    //then
    StepVerifier.create(items)
        .expectNext(title3)
        .expectNext(title1)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldFindItemsWhenDateIsNull() {
    //given
    mongoTemplate.insertAll(Arrays.asList(
        new Item(new ItemDTOBuilder().link("title1").build()),
        new Item(new ItemDTOBuilder().link("title2").build()),
        new Item(new ItemDTOBuilder().link("title3").build())
    ));

    //when
    Flux<Item> items = itemRepository.findAllOrderByPublishedDate(2, 0);
    //then
    StepVerifier.create(items)
        .expectNextCount(2)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldNotCreateItemByUniqueConstraint() {
    //given
    mongoTemplate.save(new Item(new ItemDTOBuilder().link("title1").build()));

    //expect
    assertThatThrownBy(() -> mongoTemplate.save(new Item(new ItemDTOBuilder().link("title1").build())))
        .hasMessageContaining("duplicate key error collection")
        .hasCauseInstanceOf(MongoWriteException.class);
  }

  @Test
  void shouldSetCreatedDateOnPersistNewItem() {
    Item save = mongoTemplate.save(new Item(new ItemDTOBuilder().link("title1").build()));

    assertThat(save.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldFindByBlogId() {
    //given
    mongoTemplate.insertAll(Arrays.asList(
        new Item(new ItemDTOBuilder().blogId("id1").link("link1").build()),
        new Item(new ItemDTOBuilder().blogId("id1").link("link2").build()),
        new Item(new ItemDTOBuilder().blogId("id2").link("link3").build())
    ));

    //when
    Flux<Item> byBlogId = itemRepository.findAllByBlogId("id1");
    //then
    StepVerifier.create(byBlogId)
        .expectNextCount(2L)
        .expectComplete()
        .verify();
  }
}
