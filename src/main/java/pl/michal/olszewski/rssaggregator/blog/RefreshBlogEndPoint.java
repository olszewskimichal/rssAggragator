package pl.michal.olszewski.rssaggregator.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/refresh")
@Slf4j
class RefreshBlogEndPoint {

  private final UpdateBlogService updateBlogService;

  public RefreshBlogEndPoint(UpdateBlogService updateBlogService) {
    this.updateBlogService = updateBlogService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void refreshBlog(@RequestParam(value = "blogId") Long blogId) {
    log.debug("GET - refreshBlog {}", blogId);
    updateBlogService.refreshBlogFromId(blogId);
  }

}
