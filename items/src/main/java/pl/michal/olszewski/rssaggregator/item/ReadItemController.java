package pl.michal.olszewski.rssaggregator.item;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;

@RestController()
@CrossOrigin
class ReadItemController {

  private static final Logger log = LoggerFactory.getLogger(ReadItemController.class);
  private final ReadItemService readItemService;

  ReadItemController(ReadItemService readItemService) {
    this.readItemService = readItemService;
  }

  @ApiOperation(value = "Służy do oznaczania danego wpisu jako przeczytanego")
  @SwaggerDocumented
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/api/v1/items/mark")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markItem(@RequestBody ReadItemDTO readItemDTO) {
    log.debug("START markItem id {} read {}", readItemDTO.getItemId(), readItemDTO.isRead());
    readItemService.processRequest(readItemDTO);
    log.debug("END markItem id {}", readItemDTO.getItemId());
  }
}
