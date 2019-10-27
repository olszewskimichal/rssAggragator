package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mongodb.MongoWriteException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.blog.BlogBuilder;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class ItemFinderTest extends IntegrationTestBase {

  @Autowired
  protected MongoTemplate mongoTemplate;

  @Autowired
  private ItemFinder itemFinder;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "blog");
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
    List<Item> items = itemFinder.findAllOrderByPublishedDate(2, 0);

    //then
    assertThat(items).hasSize(2).contains(newestItem, presentItem);
  }

  @Test
  void shouldFind2NewestPersistedItems() {
    //given
    mongoTemplate.save(new Item(new ItemDTOBuilder().link("title2").build()));
    Item title1 = mongoTemplate.save(new Item(new ItemDTOBuilder().link("title1").build()));
    Item title3 = mongoTemplate.save(new Item(new ItemDTOBuilder().link("title3").build()));

    //when
    List<Item> items = itemFinder.findAllOrderByCreatedAt(2, 0);

    //then
    assertThat(items).hasSize(2).contains(title3, title1);

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
    List<Item> items = itemFinder.findAllOrderByPublishedDate(2, 0);
    //then
    assertThat(items).hasSize(2);
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
    mongoTemplate.insert(new BlogBuilder().id("id1").build());
    mongoTemplate.insertAll(Arrays.asList(
        new Item(new ItemDTOBuilder().blogId("id1").link("link1").build()),
        new Item(new ItemDTOBuilder().blogId("id1").link("link2").build()),
        new Item(new ItemDTOBuilder().blogId("id2").link("link3").build())
    ));

    //when
    PageBlogItemDTO byBlogId = itemFinder.getBlogItemsForBlog("id1", null, null);
    //then
    assertThat(byBlogId.getTotalElements()).isEqualTo(2L);
  }

  @Test
  void shouldFindAllItemsFromDate() {
    //given
    mongoTemplate.save(new Item(new ItemDTOBuilder().blogId("id1").link("link1").build()));
    Item item2 = mongoTemplate.save(new Item(new ItemDTOBuilder().blogId("id2").link("link1").build()));
    mongoTemplate.save(new Item(new ItemDTOBuilder().blogId("id3").link("link1").build()));

    //when
    List<Item> itemList = itemFinder.findItemsFromDateOrderByCreatedAt(item2.getCreatedAt());

    //then
    assertThat(itemList.size()).isEqualTo(2);
  }

  @Test
  void shouldFindAllItemsByCreatedAt() {
    //given
    mongoTemplate.save(new Item(new ItemDTOBuilder().blogId("id1").link("link1").build()));
    mongoTemplate.save(new Item(new ItemDTOBuilder().blogId("id2").link("link1").build()));
    mongoTemplate.save(new Item(new ItemDTOBuilder().blogId("id3").link("link1").build()));

    //when
    List<Item> itemList = itemFinder.findItemsFromDateOrderByCreatedAt(null);

    //then
    assertThat(itemList.size()).isEqualTo(3);
  }
}
