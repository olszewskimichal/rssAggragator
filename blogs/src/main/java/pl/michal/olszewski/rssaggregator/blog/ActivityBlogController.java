package pl.michal.olszewski.rssaggregator.blog;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/blogs")
@Slf4j
@Api(value = "/api/v1/blogs")
public class ActivityBlogController {

  private final BlogActivityUpdater blogActivityUpdater;

  public ActivityBlogController(BlogActivityUpdater blogActivityUpdater) {
    this.blogActivityUpdater = blogActivityUpdater;
  }

  @PutMapping(value = "/enable/{id}")
  @ApiOperation(value = "Aktywuje blog o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Pomyslne zlecenie aktywowania bloga"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Mono<Void> enableBlogById(@PathVariable("id") String blogId) {
    log.debug("Enable blog by Id {} ", blogId);
    return blogActivityUpdater.activateBlog(blogId).then();
  }

  @PutMapping(value = "/disable/{id}")
  @ApiOperation(value = "Deaktywuje blog o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Pomyslne zlecenie deaktywowania bloga"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Mono<Void> disableBlogById(@PathVariable("id") String blogId) {
    log.debug("Disable blog by Id {} ", blogId);
    return blogActivityUpdater.deactivateBlog(blogId).then();
  }
}