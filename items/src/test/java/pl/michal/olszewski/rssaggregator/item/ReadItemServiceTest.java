package pl.michal.olszewski.rssaggregator.item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReadItemServiceTest {

  private ReadItemService readItemService;

  @Mock
  private ItemFinder itemRepository;

  @Mock
  private ItemUpdater itemUpdater;

  @BeforeEach
  void setUp() {
    readItemService = new ReadItemService(itemRepository, itemUpdater);
    given(itemUpdater.updateItem(any(Item.class))).willAnswer(i -> {
          Item argument = i.getArgument(0);
          return Mono.just(argument);
        }
    );
  }

  @Test
  void shouldMarkItemAsRead() {
    given(itemRepository.findItemById("itemId")).willReturn(Mono.just(new Item()));
    var readItemDTO = ReadItemDTO.builder().itemId("itemId").read(true).build();

    Mono<Void> result = readItemService.processRequest(readItemDTO);

    StepVerifier.create(result)
        .expectComplete()
        .verify();
    verify(itemUpdater, times(1)).updateItem(Mockito.any(Item.class));
  }

  @Test
  void shouldMarkItemAsUnread() {
    given(itemRepository.findItemById("itemId")).willReturn(Mono.just(new Item()));
    var readItemDTO = ReadItemDTO.builder().itemId("itemId").read(false).build();

    Mono<Void> result = readItemService.processRequest(readItemDTO);

    StepVerifier.create(result)
        .expectComplete()
        .verify();
    verify(itemUpdater, times(1)).updateItem(Mockito.any(Item.class));

  }

  @Test
  void shouldThrowExceptionWhenItemByIdNotExists() {
    given(itemRepository.findItemById("itemId2")).willReturn(Mono.empty());
    var readItemDTO = ReadItemDTO.builder().itemId("itemId2").read(false).build();

    Mono<Void> result = readItemService.processRequest(readItemDTO);

    StepVerifier.create(result)
        .expectError(ItemNotFoundException.class)
        .verify();
  }
}