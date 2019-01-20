package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.mongodb.MongoWriteException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogRepository;
import pl.michal.olszewski.rssaggregator.config.Profiles;

@DataMongoTest
@ActiveProfiles(Profiles.TEST)
public class ItemRepositoryTest {

  @Autowired
  protected MongoTemplate entityManager;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private BlogRepository blogRepository;

  @BeforeEach
  void setUp() {
    itemRepository.deleteAll();
    blogRepository.deleteAll();
  }

  @Test
  void shouldFind2NewestItems() {
    //given
    Blog blog = new Blog("url", "", "", "", null, null);
    Instant instant = Instant.now();
    entityManager.save(blog);
    blog.addItem(new Item(ItemDTO.builder().link("title1").date(instant).build()), itemRepository);
    blog.addItem(new Item(ItemDTO.builder().link("title2").date(instant.minusSeconds(10)).build()), itemRepository);
    blog.addItem(new Item(ItemDTO.builder().link("title3").date(instant.plusSeconds(10)).build()), itemRepository);
    entityManager.save(blog);

    //when
    List<Item> items = itemRepository.findAll(PageRequest.of(0, 2, new Sort(Direction.DESC, "date"))).getContent();

    //then
    assertAll(
        () -> assertThat(items.size()).isEqualTo(2),
        () -> assertThat(items.stream().map(Item::getDate).collect(Collectors.toList())).contains(instant, instant.plusSeconds(10))
    );
  }

  @Test
  void shouldFindItemsWhenDateIsNull() {
    //given
    Blog blog = new Blog("url", "", "", "", null, null);
    blog.addItem(new Item(ItemDTO.builder().link("title1").build()), itemRepository);
    blog.addItem(new Item(ItemDTO.builder().link("title2").build()), itemRepository);
    blog.addItem(new Item(ItemDTO.builder().link("title3").build()), itemRepository);
    entityManager.save(blog);

    //when
    List<Item> items = itemRepository.findAll(PageRequest.of(0, 2, new Sort(Direction.DESC, "date"))).getContent();

    //then
    assertThat(items.size()).isEqualTo(2);
  }

  @Test
  void shouldNotCreateItemByUniqueConstraint() {
    Blog blog = new Blog("url", "", "", "", null, null);
    blog.addItem(new Item(ItemDTO.builder().link("title1").build()), itemRepository);
    entityManager.save(blog);
    assertThatThrownBy(() -> blog.addItem(new Item(ItemDTO.builder().link("title1").description("desc").build()), itemRepository))
        .hasMessageContaining("duplicate key error collection")
        .hasCauseInstanceOf(MongoWriteException.class);
  }


}
