package pl.michal.olszewski.rssaggregator.blog.activity;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/blogs")
@Slf4j
public class ActivityBlogController {

  private final BlogActivityEventProducer eventProducer;

  public ActivityBlogController(BlogActivityEventProducer eventProducer) {
    this.eventProducer = eventProducer;
  }

  @PostMapping(value = "/enable/{id}")
  public Mono<Boolean> enableBlogById(@PathVariable("id") String blogId) {
    log.debug("Enable blog by Id {} ", blogId);
    eventProducer.writeEventToQueue(ActivateBlog.builder().blogId(blogId).occurredAt(Instant.now()).build());
    return Mono.just(Boolean.TRUE);
  }

  @PostMapping(value = "/disable/{id}")
  public Mono<Boolean> disableBlogById(@PathVariable("id") String blogId) {
    log.debug("Disable blog by Id {} ", blogId);
    eventProducer.writeEventToQueue(DeactivateBlog.builder().blogId(blogId).occurredAt(Instant.now()).build());
    return Mono.just(Boolean.TRUE);
  }
}
