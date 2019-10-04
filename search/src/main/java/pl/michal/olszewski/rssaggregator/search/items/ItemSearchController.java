package pl.michal.olszewski.rssaggregator.search.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/items/search")
class ItemSearchController {

  private static final Logger log = LoggerFactory.getLogger(ItemSearchController.class);

  private final ItemTextSearchRepository itemTextSearchRepository;

  ItemSearchController(ItemTextSearchRepository itemTextSearchRepository) {
    this.itemTextSearchRepository = itemTextSearchRepository;
  }

  @GetMapping
  public Flux<ItemSearchResult> searchItemsMatchingBy(
      @RequestParam(value = "text") String searchText,
      @RequestParam(value = "limit", required = false) Integer limit) {
    log.info("Search items matching by {} with limit {}", searchText, limit);
    return itemTextSearchRepository.findMatching(
        searchText,
        limit == null ? 10 : limit
    );
  }
}
