package pl.michal.olszewski.rssaggregator.units;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.extenstions.MockitoExtension;
import pl.michal.olszewski.rssaggregator.repository.ItemRepository;
import pl.michal.olszewski.rssaggregator.service.NewestItemService;

@ExtendWith(MockitoExtension.class)
public class NewestItemServiceTest {

  private NewestItemService itemService;

  @Mock
  private ItemRepository itemRepository;

  @BeforeEach
  public void setUp() {
    itemService = new NewestItemService(itemRepository);
  }

  @Test
  public void shouldGet10NewestItems() {
    //given
    List<Item> itemList = IntStream.rangeClosed(1, 10).parallel().mapToObj(value -> new Item(ItemDTO.builder().title("title" + value).date(Instant.now()).build())).collect(Collectors.toList());

    given(itemRepository.findAllByOrderByDateDesc(10)).willReturn(itemList.stream());
    //when
    List<ItemDTO> newestItems = itemService.getNewestItems(10);
    //then
    assertThat(newestItems).isNotNull().isNotEmpty().hasSize(10);
  }

}
