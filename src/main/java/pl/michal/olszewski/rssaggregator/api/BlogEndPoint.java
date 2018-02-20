package pl.michal.olszewski.rssaggregator.api;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.service.BlogService;

@RestController
@RequestMapping("/api/v1/blogs")
@Slf4j
public class BlogEndPoint {

  private final BlogService blogService;

  public BlogEndPoint(BlogService blogService) {
    this.blogService = blogService;
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<BlogDTO> getBlog(@PathVariable("id") Long blogId) {
    return Optional.ofNullable(blogService.getBlogDTOById(blogId))
        .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(value = "/by-name/{name}")
  public ResponseEntity<BlogDTO> getBlogByName(@PathVariable("name") String name) throws ExecutionException, InterruptedException {
    return Optional.ofNullable(blogService.getBlogDTOByName(name))
        .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BlogDTO> getBlogs(@RequestParam(value = "limit", required = false) Integer limit) {
    return blogService.getAllBlogDTOs(limit);
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateBlog(@RequestBody BlogDTO blogDTO) throws ExecutionException, InterruptedException {
    blogService.updateBlog(blogDTO);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addBlog(@RequestBody BlogDTO blogDTO) {
    blogService.createBlog(blogDTO);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBlog(@PathVariable("id") Long blogId) {
    blogService.deleteBlog(blogId);
  }

  @PostMapping(value = "/evictCache")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void evictCache() {
    blogService.evictBlogCache();
  }


}
