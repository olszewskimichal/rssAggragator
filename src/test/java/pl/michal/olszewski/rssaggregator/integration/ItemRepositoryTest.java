package pl.michal.olszewski.rssaggregator.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.repository.ItemRepository;

@DataJpaTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ItemRepositoryTest {

  @Autowired
  protected TestEntityManager entityManager;

  @Autowired
  private ItemRepository itemRepository;

  @Test
  public void shouldFind2NewestItems() {
    //given
    Blog blog = new Blog("url", "", "", "", null);
    Instant instant = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
    blog.addItem(new Item(ItemDTO.builder().title("title1").date(instant).build()));
    blog.addItem(new Item(ItemDTO.builder().title("title2").date(instant.minusSeconds(10)).build()));
    blog.addItem(new Item(ItemDTO.builder().title("title3").date(instant.plusSeconds(10)).build()));
    entityManager.persistAndFlush(blog);

    //when
    Page<Item> items = itemRepository.findAllByOrderByDateDesc(new PageRequest(0, 2));

    //then
    assertThat(items.getContent().size()).isEqualTo(2);
    assertThat(items.getContent().stream().map(Item::getDate).collect(Collectors.toList())).contains(instant, instant.plusSeconds(10));
  }

  @Test
  public void shouldFindItemsWhenDateIsNull(){
    //given
    Blog blog = new Blog("url", "", "", "", null);
    blog.addItem(new Item(ItemDTO.builder().title("title1").build()));
    blog.addItem(new Item(ItemDTO.builder().title("title2").build()));
    blog.addItem(new Item(ItemDTO.builder().title("title3").build()));
    entityManager.persistAndFlush(blog);

    //when
    Page<Item> items = itemRepository.findAllByOrderByDateDesc(new PageRequest(0, 2));

    //then
    assertThat(items.getContent().size()).isEqualTo(2);
    items.getContent().forEach(System.out::println);
  }


}
