package pl.michal.olszewski.rssaggregator.item;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.ogtags.OgTagInfoUpdater;

@Service
class UpdateItemsImageUrlWorker {

  private final ItemFinder itemFinder;
  private final OgTagInfoUpdater ogTagInfoUpdater;
  private final ItemRepository itemRepository;

  UpdateItemsImageUrlWorker(ItemFinder itemFinder, OgTagInfoUpdater ogTagInfoUpdater, ItemRepository itemRepository) {
    this.itemFinder = itemFinder;
    this.ogTagInfoUpdater = ogTagInfoUpdater;
    this.itemRepository = itemRepository;
  }

  void updateImageUrls() {
    itemFinder.findItemsFromDateOrderByCreatedAt(Instant.now().minus(100, DAYS)).stream()
        .map(item -> {
          item.updateImageUrl(ogTagInfoUpdater.updateItemByOgTagInfo(ItemToDtoMapper.mapItemToItemDTO(item)).getImageURL());
          return item;
        }).forEach(itemRepository::save);
  }
}
