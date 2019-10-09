package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.ogtags.OgTagInfoUpdater;

@Service
class UpdateItemsImageUrlWorker {

  private final ItemFinder itemFinder;
  private final OgTagInfoUpdater ogTagInfoUpdater;
  private final ItemRepositorySync repositorySync;

  UpdateItemsImageUrlWorker(ItemFinder itemFinder, OgTagInfoUpdater ogTagInfoUpdater, ItemRepositorySync repositorySync) {
    this.itemFinder = itemFinder;
    this.ogTagInfoUpdater = ogTagInfoUpdater;
    this.repositorySync = repositorySync;
  }

  void updateImageUrls() {
    itemFinder.findItemsFromDateOrderByCreatedAt(Instant.now().minus(100, ChronoUnit.DAYS)).stream()
        .map(item -> {
          item.updateImageUrl(ogTagInfoUpdater.updateItemByOgTagInfo(ItemToDtoMapper.mapItemToItemDTO(item)).getImageURL());
          return item;
        }).forEach(repositorySync::save);
  }
}
