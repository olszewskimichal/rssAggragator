package pl.michal.olszewski.rssaggregator.item;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.ItemRepository;

@Service
@Transactional(readOnly = true)
@Slf4j
class NewestItemService {

  private final ItemRepository itemRepository;

  public NewestItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  List<ItemDTO> getNewestItems(int size) {
    log.debug("Pobieram wpisy z limitem {}", size);
    return itemRepository.findAllByOrderByDateDesc(size)
        .map(v -> new ItemDTO(v.getTitle(), v.getDescription(), v.getLink(), v.getDate(), v.getAuthor()))
        .collect(Collectors.toList());
  }

  @CacheEvict(value = {"items"}, allEntries = true)
  public void evictItemsCache() {
    log.debug("Czyszcze cache dla itemów");
  }
}