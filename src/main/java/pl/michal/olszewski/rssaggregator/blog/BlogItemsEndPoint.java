package pl.michal.olszewski.rssaggregator.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/blogs")
@Slf4j
class BlogItemsEndPoint {

  private final BlogService blogService;

  public BlogItemsEndPoint(BlogService blogService) {
    this.blogService = blogService;
  }

  @GetMapping(value = "/{id}/items")
  public Flux<ItemDTO> getBlogItems(@PathVariable("id") String blogId) {
    log.debug("GET blogItems for id {}", blogId);
    return blogService.getBlogItemsForBlog(blogId);
  }
}
