package pl.michal.olszewski.rssaggregator.item;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
    given(itemUpdater.updateItem(any(Item.class))).willAnswer(i -> i.getArgument(0));
  }

  @Test
  void shouldMarkItemAsRead() {
    given(itemRepository.findItemById("itemId")).willReturn(Optional.of(new Item()));
    var readItemDTO = new ReadItemDTO("itemId", true);

    //when
    readItemService.processRequest(readItemDTO);

    //then
    verify(itemUpdater, times(1)).updateItem(Mockito.any(Item.class));
  }

  @Test
  void shouldMarkItemAsUnread() {
    given(itemRepository.findItemById("itemId")).willReturn(Optional.of(new Item()));
    var readItemDTO = new ReadItemDTO("itemId", false);

    //when
    readItemService.processRequest(readItemDTO);

    //then
    verify(itemUpdater, times(1)).updateItem(Mockito.any(Item.class));

  }

  @Test
  void shouldThrowExceptionWhenItemByIdNotExists() {
    given(itemRepository.findItemById("itemId2")).willReturn(Optional.empty());
    var readItemDTO = new ReadItemDTO("itemId2", false);

    //when
    //then
    assertThrows(ItemNotFoundException.class, () -> readItemService.processRequest(readItemDTO));
  }
}