package pl.michal.olszewski.rssaggregator.blog.activity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/blogs")
@Slf4j
@Api(value = "/api/v1/blogs")
public class ActivityBlogController {

  private final BlogActivityEventProducer eventProducer;

  public ActivityBlogController(BlogActivityEventProducer eventProducer) {
    this.eventProducer = eventProducer;
  }

  @PostMapping(value = "/enable/{id}")
  @ApiOperation(value = "Aktywuje blog o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Pomyslne zlecenie aktywowania bloga"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Mono<Void> enableBlogById(@PathVariable("id") String blogId) {
    log.debug("Enable blog by Id {} ", blogId);
    return Mono.fromRunnable(
        () -> eventProducer.writeEventToQueue(ActivateBlog.builder().blogId(blogId).occurredAt(Instant.now()).build())
    );
  }

  @PostMapping(value = "/disable/{id}")
  @ApiOperation(value = "Deaktywuje blog o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Pomyslne zlecenie deaktywowania bloga"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Mono<Void> disableBlogById(@PathVariable("id") String blogId) {
    log.debug("Disable blog by Id {} ", blogId);
    return Mono.fromRunnable(
        () -> eventProducer.writeEventToQueue(DeactivateBlog.builder().blogId(blogId).occurredAt(Instant.now()).build())
    );
  }
}
