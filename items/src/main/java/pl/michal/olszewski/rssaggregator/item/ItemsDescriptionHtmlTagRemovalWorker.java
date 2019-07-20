package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class ItemsDescriptionHtmlTagRemovalWorker {

  private final ItemRepository itemRepository;

  public ItemsDescriptionHtmlTagRemovalWorker(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  ParallelFlux<Item> processAllItemsAndRemoveHtmlTagsFromDescription() {
    log.info("Remove html tags from items descriptions");
    return itemRepository.findAll()
        .parallel()
        .runOn(Schedulers.parallel())
        .flatMap(this::updateItemDescription);
  }

  private Mono<Item> updateItemDescription(Item item) {
    log.debug("Update Item  id = {} title = {}", item.getId(), item.getTitle());
    log.info("Description before change {}", item.getDescription());
    item.setDescription(HtmlTagRemover.removeHtmlTagFromDescription(item.getDescription()));
    log.info("Description after change {}", item.getDescription());
    return itemRepository.save(item);
  }
}
