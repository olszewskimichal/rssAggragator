package pl.michal.olszewski.rssaggregator.blog.failure;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/blogs/failure")
@Slf4j
public class FailureUpdatesBlogController {

  private final BlogUpdateFailedEventAggregator failedEventAggregator;

  public FailureUpdatesBlogController(BlogUpdateFailedEventAggregator failedEventAggregator) {
    this.failedEventAggregator = failedEventAggregator;
  }

  @GetMapping
  @ApiOperation(value = "Zwraca wszystkie błedy które wystąpiły podczas aktualizacji blogów w pogrupowaniu na blogId i errorMessage")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Zwraca liste elementow wszystkich bledow", response = UpdateBlogFailureCount.class),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Flux<UpdateBlogFailureCount> getAllFailure() {
    return failedEventAggregator.aggregateAllFailureOfBlogs();
  }

  @GetMapping(value = "/lastDay")
  @ApiOperation(value = "Zwraca wszystkie błedy które wystąpiły podczas aktualizacji blogów w pogrupowaniu na blogId i errorMessage z ostatnich 24h")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Zwraca liste elementow wszystkich bledow z ostatnich 24h", response = UpdateBlogFailureCount.class),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Flux<UpdateBlogFailureCount> getFailureFromLast24() {
    return failedEventAggregator.aggregateAllFailureOfBlogsFromPrevious24h();
  }
}
