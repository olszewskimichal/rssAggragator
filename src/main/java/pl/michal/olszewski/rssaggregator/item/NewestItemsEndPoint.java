package pl.michal.olszewski.rssaggregator.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/items")
@Slf4j
class NewestItemsEndPoint {

  private final NewestItemService itemService;

  public NewestItemsEndPoint(NewestItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping
  @Transactional
  public Flux<ItemDTO> getItems(@RequestParam(value = "limit", required = false) Integer limit) {
    return itemService.getNewestItems(limit == null ? 10 : limit);
  }
}
