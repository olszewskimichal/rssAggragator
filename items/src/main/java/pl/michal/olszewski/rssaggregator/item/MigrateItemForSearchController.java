package pl.michal.olszewski.rssaggregator.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

class MigrateItemForSearchController {

  private static final Logger log = LoggerFactory.getLogger(MigrateItemForSearchController.class);

  private final MigrateItemsForSearchWorker searchWorker;

  MigrateItemForSearchController(MigrateItemsForSearchWorker searchWorker) {
    this.searchWorker = searchWorker;
  }

  @GetMapping(value = "/api/v1/blogs/items/migrate")
  public void migrateItems() {
    log.debug("START migrateItems");
    searchWorker.migrateItemsForSearch();
    log.debug("STOP migrateItems");
  }
}
