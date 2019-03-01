package pl.michal.olszewski.rssaggregator.blog;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
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
  public Mono<BlogAggregationDTO> getBlog(@PathVariable("id") String blogId) {
    log.debug("GET blog by id {}", blogId);
    return blogService.getBlogDTOById(blogId)
        .map(this::addLinkToBlogItems);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<BlogAggregationDTO> getBlogs() {
    log.debug("GET blogs");
    return blogService.getAllBlogDTOs()
        .map(this::addLinkToSelf)
        .map(this::addLinkToBlogItems);
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Blog> updateBlog(@RequestBody BlogDTO blogDTO) {
    log.debug("PUT - updateBlog {}", blogDTO.getName());
    log.trace("PUT - updateBlog {}", blogDTO);
    return blogService.updateBlog(blogDTO);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Blog> addBlog(@RequestBody BlogDTO blogDTO) {
    log.debug("POST - addBlog {}", blogDTO.getName());
    log.trace("POST - addBlog {}", blogDTO);
    return blogService.getBlogOrCreate(blogDTO);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteBlog(@PathVariable("id") String blogId) {
    log.debug("DELETE - deleteBlog {}", blogId);
    return blogService.deleteBlog(blogId);
  }

  @PostMapping(value = "/evictCache")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void evictCache() {
    log.debug("POST - evict Cache");
    blogService.evictBlogCache();
  }

  private UriComponents getRequestUriComponents() {
    try {
      return ServletUriComponentsBuilder.fromCurrentRequest().build();
    } catch (IllegalStateException ex) {
      return ServletUriComponentsBuilder.fromUriString("localhost").build();
    }
  }

  private BlogAggregationDTO addLinkToSelf(BlogAggregationDTO blog) {
    if (blog.getLink("self") == null) {
      Link link = linkTo(methodOn(BlogEndPoint.class)
          .getBlog(blog.getBlogId())).withSelfRel();
      blog.add(link);
    }
    return blog;
  }

  private BlogAggregationDTO addLinkToBlogItems(BlogAggregationDTO blog) {
    if (blog.getLink("items") == null) {
      Link link = linkTo(methodOn(BlogItemsEndPoint.class)
          .getBlogItems(blog.getBlogId())).withRel("items");
      blog.add(link);
    }
    return blog;
  }

}
