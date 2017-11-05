package pl.michal.olszewski.rssaggregator.api;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.service.NewestItemService;

@RestController
@RequestMapping("/api/v1/items")
@Slf4j
public class NewestItemsEndPoint {

  private final NewestItemService itemService;

  public NewestItemsEndPoint(NewestItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ItemDTO> getItems(@RequestParam(value = "limit", required = false) Integer limit) {
    return itemService.getNewestItems(limit == null ? 10 : limit);
  }
}
