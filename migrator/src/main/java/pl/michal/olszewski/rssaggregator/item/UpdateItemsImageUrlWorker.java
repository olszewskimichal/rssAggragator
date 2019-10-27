package pl.michal.olszewski.rssaggregator.item;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Instant;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.ogtags.OgTagInfoUpdater;

@Service
class UpdateItemsImageUrlWorker {

  private final OgTagInfoUpdater ogTagInfoUpdater;
  private final ItemRepository itemRepository;

  UpdateItemsImageUrlWorker(OgTagInfoUpdater ogTagInfoUpdater, ItemRepository itemRepository) {
    this.ogTagInfoUpdater = ogTagInfoUpdater;
    this.itemRepository = itemRepository;
  }

  void updateImageUrls() {
    itemRepository.findItemsFromDateOrderByCreatedAt(Instant.now().minus(100, DAYS)).stream()
        .peek(item -> item.updateImageUrl(ogTagInfoUpdater.updateItemByOgTagInfo(ItemToDtoMapper.mapItemToItemDTO(item)).getImageURL()))
        .forEach(itemRepository::save);
  }
}
