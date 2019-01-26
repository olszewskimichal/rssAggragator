package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
        Flux<Item> items = itemRepository.findAllNew(size);
        return items.map(v -> new ItemDTO(v.getTitle(), v.getDescription(), v.getLink(), v.getDate(), v.getAuthor()));
    }

    @CacheEvict(value = {"items"}, allEntries = true)
    public void evictItemsCache() {
        log.debug("Czyszcze cache dla itemów");
    }
}
