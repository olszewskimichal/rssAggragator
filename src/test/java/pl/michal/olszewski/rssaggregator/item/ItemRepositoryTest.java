package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mongodb.MongoWriteException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.config.Profiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles(Profiles.TEST)
public class ItemRepositoryTest {

  @Autowired
  protected MongoTemplate mongoTemplate;

  @Autowired
  private ItemRepository itemRepository;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
    mongoTemplate.remove(new Query(), "blog");
  }

  //TODO za dlugie given
  @Test
  void shouldFind2NewestItems() {
    //given
    Blog blog = new Blog("url", "", "", "", null, null);
    Instant instant = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    mongoTemplate.save(blog);
    Item title1 = new Item(ItemDTO.builder().link("title1").date(instant).build());
    Item title3 = new Item(ItemDTO.builder().link("title3").date(instant.plusSeconds(10)).build());
    blog.addItem(title1, mongoTemplate);
    blog.addItem(new Item(ItemDTO.builder().link("title2").date(instant.minusSeconds(10)).build()), mongoTemplate);
    blog.addItem(title3, mongoTemplate);
    mongoTemplate.save(blog);

    //when
    Flux<Item> items = itemRepository.findAllNew(2);

    //then
    StepVerifier.create(items)
        .expectNextCount(2)
        .expectComplete()
        .verify();
    StepVerifier.create(items)
        .expectNext(title3)
        .expectNext(title1)
        .expectComplete()
        .verify();
  }

  //TODO za dlugie given
  @Test
  void shouldFindItemsWhenDateIsNull() {
    //given
    Blog blog = new Blog("url", "", "", "", null, null);
    blog.addItem(new Item(ItemDTO.builder().link("title1").date(Instant.now()).build()), mongoTemplate);
    blog.addItem(new Item(ItemDTO.builder().link("title2").date(Instant.now()).build()), mongoTemplate);
    blog.addItem(new Item(ItemDTO.builder().link("title3").date(Instant.now()).build()), mongoTemplate);
    mongoTemplate.save(blog);

    //when
    Flux<Item> items = itemRepository.findAllNew(2);
    //then
    StepVerifier.create(items)
        .expectNextCount(2)
        .expectComplete()
        .verify();
  }

  @Test
  void shouldNotCreateItemByUniqueConstraint() { //TODO zapiac sie jakas fabryka
    //given
    Blog blog = new Blog("url", "", "", "", null, null);
    blog.addItem(new Item(ItemDTO.builder().link("title1").build()), mongoTemplate);
    mongoTemplate.save(blog);

    //expect
    assertThatThrownBy(() -> blog.addItem(new Item(ItemDTO.builder().link("title1").description("desc").build()), mongoTemplate))
        .hasMessageContaining("duplicate key error collection")
        .hasCauseInstanceOf(MongoWriteException.class);
  }


}
