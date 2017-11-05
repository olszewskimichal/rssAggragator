package pl.michal.olszewski.rssaggregator.units;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.repository.ItemRepository;
import pl.michal.olszewski.rssaggregator.service.NewestItemService;

public class NewestItemServiceTest {

  private NewestItemService itemService;

  @Mock
  private ItemRepository itemRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    itemService = new NewestItemService(itemRepository);
  }

  @Test
  public void shouldGet10NewestItems() {
    //given
    List<Item> itemList = IntStream.rangeClosed(1, 10).mapToObj(value -> new Item(ItemDTO.builder().title("title" + value).date(Instant.now()).build())).collect(Collectors.toList());

    given(itemRepository.findAllByOrderByDateDesc(new PageRequest(0, 10))).willReturn(new PageImpl<>(itemList));
    //when
    List<ItemDTO> newestItems = itemService.getNewestItems(10);
    //then
    assertThat(newestItems).isNotNull().isNotEmpty().hasSize(10);
  }

}
