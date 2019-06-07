package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@Transactional(readOnly = true)
@Slf4j
class NewestItemService {

  private final ItemRepository itemRepository;

  public NewestItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  Flux<ItemDTO> getNewestItemsOrderByPublishedDate(int size, int page) {
    log.debug("Pobieram wpisy z limitem {} strona {}", size, page);
    return itemRepository.findAllOrderByPublishedDate(size, page)
        .map(ItemDTO::new);
  }

  Flux<ItemDTO> getNewestItemsOrderByCreatedAt(int size, int page) {
    log.debug("Pobieram najnowsze wpisy z limitem {} strona {}", size, page);
    return itemRepository.findAllOrderByCreatedAt(size, page)
        .map(ItemDTO::new);
  }
}
