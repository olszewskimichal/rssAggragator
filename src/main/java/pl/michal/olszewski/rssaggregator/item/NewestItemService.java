package pl.michal.olszewski.rssaggregator.item;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

  Flux<ItemDTO> getNewestItems(int size) {
    log.debug("Pobieram wpisy z limitem {}", size);
    Page<Item> items = itemRepository.findAll(PageRequest.of(0, size, new Sort(Direction.DESC, "date")));
    return Flux.fromIterable(items.getContent())
        .map(v -> new ItemDTO(v.getTitle(), v.getDescription(), v.getLink(), v.getDate(), v.getAuthor()));
  }

  @CacheEvict(value = {"items"}, allEntries = true)
  public void evictItemsCache() {
    log.debug("Czyszcze cache dla itemów");
  }
}
