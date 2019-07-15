package pl.michal.olszewski.rssaggregator.blog.items;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/blogs")
@Slf4j
public class BlogItemsController {

  private final BlogItemsService blogService;

  public BlogItemsController(BlogItemsService blogService) {
    this.blogService = blogService;
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
    String correlationId = UUID.randomUUID().toString();
    log.debug("START GET blogItems for id {} correlationId {}", blogId, correlationId);
    return blogService.getBlogItemsForBlog(blogId, correlationId)
        .doOnComplete(() -> log.debug("END GET blogItems for id {} correlationId {}", blogId, correlationId));
  }
}
