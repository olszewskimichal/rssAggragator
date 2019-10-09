package pl.michal.olszewski.rssaggregator.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/blogs/items")
class MigrateController {

  private static final Logger log = LoggerFactory.getLogger(MigrateController.class);

  private final MigrateItemsForSearchWorker searchWorker;
  private final UpdateItemsImageUrlWorker itemsImageUrlWorker;

  MigrateController(MigrateItemsForSearchWorker searchWorker, UpdateItemsImageUrlWorker itemsImageUrlWorker) {
    this.searchWorker = searchWorker;
    this.itemsImageUrlWorker = itemsImageUrlWorker;
  }

  @GetMapping(value = "migrate")
  public void migrateItems() {
    log.debug("START migrateItems");
    searchWorker.migrateItemsForSearch();
    log.debug("STOP migrateItems");
  }

  @GetMapping(value = "updateImages")
  public void updateImages() {
    log.debug("START updateImages");
    itemsImageUrlWorker.updateImageUrls();
    log.debug("STOP updateImages");
  }
}
