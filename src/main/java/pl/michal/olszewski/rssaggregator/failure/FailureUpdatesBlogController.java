package pl.michal.olszewski.rssaggregator.failure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.events.failed.BlogUpdateFailedEventAggregator;
import pl.michal.olszewski.rssaggregator.events.failed.UpdateBlogFailureCount;
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
  Flux<UpdateBlogFailureCount> getAllFailure() {
    return failedEventAggregator.aggregateAllFailureOfBlogs();
  }

  @GetMapping(value = "/lastDay")
  Flux<UpdateBlogFailureCount> getFailureFromLast24() {
    return failedEventAggregator.aggregateAllFailureOfBlogsFromPrevious24h();
  }
}
