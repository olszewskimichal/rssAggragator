package pl.michal.olszewski.rssaggregator.item;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;

@RestController
@RequestMapping("/api/v1/items")
@CrossOrigin
class NewestItemsController {

  private static final Logger log = LoggerFactory.getLogger(NewestItemsController.class);

  private final NewestItemService itemService;

  public NewestItemsController(NewestItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping
  @ApiOperation(value = "Służy do pobierania najnowszych wpisow na blogach posortowanych wedlug daty publikacji")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Lista wpisow na blogach", response = PageItemDTO.class),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public PageItemDTO getItemsOrderByPublishedDate(
      @ApiParam(name = "limit", value = "okresla ile elementow chcemy pobrac")
      @RequestParam(value = "limit", required = false)
          Integer limit,
      @ApiParam(name = "page")
      @RequestParam(value = "page", required = false)
          Integer page) {
    log.debug("GET ItemsOrderByPublishedDate with limit {} and page {}", limit, page);
    return itemService.getNewestItemsOrderByPublishedDate(limit, page);
  }

  @GetMapping("/createdAt")
  @ApiOperation(value = "Służy do pobierania najnowszych wpisow na blogach posortowanych wedlug daty zapisania do bazy danych")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Lista wpisow na blogach", response = PageItemDTO.class),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public PageItemDTO getItemsOrderByCreatedAt(
      @ApiParam(name = "limit", value = "okresla ile elementow chcemy pobrac")
      @RequestParam(value = "limit", required = false)
          Integer limit,
      @ApiParam(name = "page")
      @RequestParam(value = "page", required = false)
          Integer page
  ) {
    log.debug("GET ItemsOrderByCreatedAt with limit {} and page {}", limit, page);
    return itemService.getNewestItemsOrderByCreatedAt(limit, page);
  }
}
