package pl.michal.olszewski.rssaggregator.item;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.util.Page;
import reactor.core.publisher.Mono;

@Service
class NewestItemService {

  private static final Logger log = LoggerFactory.getLogger(NewestItemService.class);
  private final ItemFinder itemFinder;

  NewestItemService(ItemFinder itemFinder) {
    this.itemFinder = itemFinder;
  }

  Mono<PageItemDTO> getNewestItemsOrderByPublishedDate(Integer size, Integer page) {
    Page pageable = new Page(size, page);
    log.debug("Pobieram wpisy z limitem {} strona {}", pageable.getLimit(), pageable.getPageForHuman());
    List<ItemDTO> items = itemFinder.findAllOrderByPublishedDate(pageable.getLimit(), pageable.getPageForSearch())
        .stream()
        .map(ItemToDtoMapper::mapItemToItemDTO)
        .collect(Collectors.toList());
    return Mono.just(new PageItemDTO(items, itemFinder.countAllItems()));
  }

  Mono<PageItemDTO> getNewestItemsOrderByCreatedAt(Integer size, Integer page) {
    Page pageable = new Page(size, page);
    log.debug("Pobieram najnowsze wpisy z limitem {} strona {}", pageable.getLimit(), pageable.getPageForHuman());
    List<ItemDTO> items = itemFinder.findAllOrderByCreatedAt(pageable.getLimit(), pageable.getPageForSearch())
        .stream()
        .map(ItemToDtoMapper::mapItemToItemDTO)
        .collect(Collectors.toList());
    return Mono.just(new PageItemDTO(items, itemFinder.countAllItems()));
  }


}
