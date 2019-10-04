package pl.michal.olszewski.rssaggregator.item;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogItemsController {

  private static final Logger log = LoggerFactory.getLogger(BlogItemsController.class);
  private final ItemFinder itemFinder;

  public BlogItemsController(ItemFinder itemFinder) {
    this.itemFinder = itemFinder;
  }

  @GetMapping(value = "/{id}/items")
  @ApiOperation(value = "Zwraca wszystkie wpisy z bloga o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Zwraca liste wszystkich wpisów z danego bloga", response = BlogItemDTO.class),
      @ApiResponse(code = 404, message = "Blog o podanym id nie istnieje"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Flux<BlogItemDTO> getBlogItems(@PathVariable("id") String blogId) {
    log.debug("START GET blogItems for id {}", blogId);
    return itemFinder.getBlogItemsForBlog(blogId)
        .doOnComplete(() -> log.debug("END GET blogItems for id {}", blogId));
  }
}
