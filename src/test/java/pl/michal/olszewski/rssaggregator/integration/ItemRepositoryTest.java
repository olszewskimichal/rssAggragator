package pl.michal.olszewski.rssaggregator.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.michal.olszewski.rssaggregator.config.Profiles;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.repository.ItemRepository;

@DataJpaTest
@ActiveProfiles(Profiles.TEST)
@RunWith(JUnitPlatform.class)
@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
public class ItemRepositoryTest {

  @Autowired
  protected TestEntityManager entityManager;

  @Autowired
  private ItemRepository itemRepository;

  @Test
  void shouldFind2NewestItems() {
    //given
    Blog blog = new Blog("url", "", "", "", null, null);
    Instant instant = Instant.now();
    blog.addItem(new Item(ItemDTO.builder().title("title1").date(instant).build()));
    blog.addItem(new Item(ItemDTO.builder().title("title2").date(instant.minusSeconds(10)).build()));
    blog.addItem(new Item(ItemDTO.builder().title("title3").date(instant.plusSeconds(10)).build()));
    entityManager.persistAndFlush(blog);

    //when
    List<Item> items = itemRepository.findAllByOrderByDateDesc(2).collect(Collectors.toList());

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
    blog.addItem(new Item(ItemDTO.builder().title("title1").build()));
    blog.addItem(new Item(ItemDTO.builder().title("title2").build()));
    blog.addItem(new Item(ItemDTO.builder().title("title3").build()));
    entityManager.persistAndFlush(blog);

    //when
    List<Item> items = itemRepository.findAllByOrderByDateDesc(2).collect(Collectors.toList());

    //then
    assertThat(items.size()).isEqualTo(2);
  }

  @Test
  void shouldNotCreateItemByUniqueConstraint() {
    Blog blog = new Blog("url", "", "", "", null, null);
    blog.addItem(new Item(ItemDTO.builder().link("title1").build()));
    entityManager.persistAndFlush(blog);
    blog.addItem(new Item(ItemDTO.builder().link("title1").description("desc").build()));
    assertThatThrownBy(() -> entityManager.persistAndFlush(blog)).hasMessageContaining("could not execute").hasCauseInstanceOf(ConstraintViolationException.class);
  }


}
