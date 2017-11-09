package pl.michal.olszewski.rssaggregator.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.service.UpdateBlogService;

@RestController
@RequestMapping("/api/v1/refresh")
public class RefreshBlogEndPoint {

  private final UpdateBlogService updateBlogService;

  public RefreshBlogEndPoint(UpdateBlogService updateBlogService) {
    this.updateBlogService = updateBlogService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateBlog(@RequestParam(value = "blogId", required = true) Long blogId) {
    updateBlogService.updateBlogFromId(blogId);
  }

}
