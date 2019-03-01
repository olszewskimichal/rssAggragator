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
    UriComponents uriComponents = getRequestUriComponents();
    return blogService.getBlogDTOById(blogId)
        .map(blog -> addLinkToBlogItems(blog, uriComponents));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<BlogAggregationDTO> getBlogs() {
    log.debug("GET blogs");
    UriComponents uriComponents = getRequestUriComponents();
    return blogService.getAllBlogDTOs()
        .map(blog -> addLinkToSelf(blog, uriComponents))
        .map(blog -> addLinkToBlogItems(blog, uriComponents));
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

  private BlogAggregationDTO addLinkToSelf(BlogAggregationDTO blog, UriComponents uriComponents) {
    Link link = linkTo(methodOn(BlogEndPoint.class)
        .getBlog(blog.getBlogId())).withSelfRel();
    return composeLink(blog, uriComponents, link, "self");
  }

  private BlogAggregationDTO addLinkToBlogItems(BlogAggregationDTO blog, UriComponents uriComponents) {
    Link link = linkTo(methodOn(BlogItemsEndPoint.class)
        .getBlogItems(blog.getBlogId())).withRel("items");
    return composeLink(blog, uriComponents, link, "items");
  }

  private BlogAggregationDTO composeLink(BlogAggregationDTO blog, UriComponents uriComponents, Link link, String linkName) {
    Link link1 = blog.getLink(linkName);
    if (link1 == null) {
      String url = "http://" + uriComponents.getHost() + ":" + uriComponents.getPort() + link.getHref();
      blog.add(new Link(url).withRel(linkName));
    }
    return blog;
  }

}
