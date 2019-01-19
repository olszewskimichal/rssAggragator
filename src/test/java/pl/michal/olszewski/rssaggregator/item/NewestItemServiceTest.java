package pl.michal.olszewski.rssaggregator.item;

import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import pl.michal.olszewski.rssaggregator.extenstions.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class NewestItemServiceTest {

  private NewestItemService itemService;

  @Mock
  private ItemRepository itemRepository;

  @BeforeEach
  void setUp() {
    itemService = new NewestItemService(itemRepository);
  }

  @Test
  void shouldGet10NewestItems() {
    //given
    List<Item> itemList = IntStream.rangeClosed(1, 10).parallel().mapToObj(value -> new Item(ItemDTO.builder().title("title" + value).date(Instant.now()).build())).collect(Collectors.toList());

    given(itemRepository.findAll(PageRequest.of(0, 10, new Sort(Direction.DESC, "date")))).willReturn(new PageImpl<>(itemList));
    //when
    Flux<ItemDTO> newestItems = itemService.getNewestItems(10);
    //then
    StepVerifier.create(newestItems)
        .recordWith(ArrayList::new)
        .expectNextCount(10)
        .expectComplete()
        .verify();
  }

}
