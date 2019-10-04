package pl.michal.olszewski.rssaggregator.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
class NewestItemService {

  private static final Logger log = LoggerFactory.getLogger(NewestItemService.class);
  private final ItemFinder itemFinder;

  NewestItemService(ItemFinder itemFinder) {
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
