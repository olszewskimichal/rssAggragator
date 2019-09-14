package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
class MigrateItemForSearchController {

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
