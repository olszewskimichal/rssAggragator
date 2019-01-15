package pl.michal.olszewski.rssaggregator.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/blogs")
@Slf4j
class BlogEndPoint {

  private final BlogService blogService;

  public BlogEndPoint(BlogService blogService) {
    this.blogService = blogService;
  }

  @GetMapping(value = "/{id}")
  public Mono<BlogDTO> getBlog(@PathVariable("id") Long blogId) {
    return blogService.getBlogDTOById(blogId);
  }

  @GetMapping(value = "/by-name/{name}")
  public Mono<BlogDTO> getBlogByName(@PathVariable("name") String name) {
    return blogService.getBlogDTOByName(name);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<BlogDTO> getBlogs(@RequestParam(value = "limit", required = false) Integer limit) {
    return blogService.getAllBlogDTOs(limit);
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateBlog(@RequestBody BlogDTO blogDTO) {
    blogService.updateBlog(blogDTO);
  } //TODO fixnac

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
