package pl.michal.olszewski.rssaggregator.blog.search.items;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/items/search")
public class ItemSearchController {

  private final ItemTextSearchRepository itemTextSearchRepository;

  public ItemSearchController(ItemTextSearchRepository itemTextSearchRepository) {
    this.itemTextSearchRepository = itemTextSearchRepository;
  }

  @GetMapping
  Flux<ItemSearchResult> searchItemsMatchingBy(
      @RequestParam(value = "text") String searchText,
      @RequestParam(value = "limit", required = false) Integer limit) {
    return itemTextSearchRepository.findMatching(
        searchText,
        limit == null ? 10 : limit
    );
  }
}
