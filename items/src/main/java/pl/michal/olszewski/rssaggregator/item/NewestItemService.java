package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
class NewestItemService {

  private final ItemFinder itemFinder;

  public NewestItemService(ItemFinder itemFinder) {
    this.itemFinder = itemFinder;
  }

  Flux<ItemDTO> getNewestItemsOrderByPublishedDate(int size, int page) {
    log.debug("Pobieram wpisy z limitem {} strona {}", size, page);
    return itemFinder.findAllOrderByPublishedDate(size, page)
        .map(ItemToDtoMapper::mapItemToItemDTO);
  }

  Flux<ItemDTO> getNewestItemsOrderByCreatedAt(int size, int page) {
    log.debug("Pobieram najnowsze wpisy z limitem {} strona {}", size, page);
    return itemFinder.findAllOrderByCreatedAt(size, page)
        .map(ItemToDtoMapper::mapItemToItemDTO);
  }


}
