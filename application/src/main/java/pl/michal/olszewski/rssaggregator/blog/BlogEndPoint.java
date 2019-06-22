package pl.michal.olszewski.rssaggregator.blog;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.UUID;
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
import pl.michal.olszewski.rssaggregator.blog.items.BlogItemsEndPoint;
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;
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
  @ApiOperation(value = "Sluzy do pobierania informacji na temat bloga o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Zwraca informacje na temat bloga", response = BlogAggregationDTO.class),
      @ApiResponse(code = 404, message = "Blog o podanym id nie istnieje"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Mono<BlogAggregationDTO> getBlog(@PathVariable("id") String blogId) {
    String correlationID = UUID.randomUUID().toString();
    log.debug("START GET blog by id {} correlationId {}", blogId, correlationID);
    return blogService.getBlogDTOById(blogId, correlationID)
        .map(this::addLinkToBlogItems)
        .doOnSuccess(result -> log.trace("END GET blog by id {} correlationId {}", blogId, correlationID))
        .doOnError(error -> log.error("ERROR GET blog by id {} correlationId {}", blogId, correlationID, error));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Sluzy do pobierania informacji na temat wszystkich blogow")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Zwraca informacje na temat blogow", response = BlogAggregationDTO.class),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Flux<BlogAggregationDTO> getBlogs() {
    String correlationID = UUID.randomUUID().toString();
    log.debug("START GET blogs correlationId {}", correlationID);
    return blogService.getAllBlogDTOs(correlationID)
        .map(this::addLinkToSelf)
        .map(this::addLinkToBlogItems)
        .doOnComplete(() -> log.debug("END GET blogs - correlationId {}", correlationID))
        .doOnError(error -> log.error("ERROR GET blogs - correlationId {}", correlationID, error));
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Sluzy do aktualizacji bloga o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Zwraca informacje na temat bloga", response = BlogDTO.class),
      @ApiResponse(code = 404, message = "Blog o podanym id nie istnieje"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Mono<BlogDTO> updateBlog(@RequestBody BlogDTO blogDTO) {
    String correlationId = UUID.randomUUID().toString();
    log.debug("PUT - updateBlog {} correlationId {}", blogDTO.getName(), correlationId);
    log.trace("PUT - updateBlog {} correlationId {}", blogDTO, correlationId);
    return blogService.updateBlog(blogDTO, correlationId)
        .doOnSuccess(blog -> log.debug("END updateBlog - correlationId {}", correlationId))
        .doOnError(error -> log.error("ERROR updateBlog - correlationId {}", correlationId, error));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Sluzy do dodawania nowego bloga")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Zwraca informacje na temat bloga", response = BlogDTO.class),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public Mono<BlogDTO> addBlog(@RequestBody BlogDTO blogDTO) {
    String correlationId = UUID.randomUUID().toString();
    log.debug("POST - addBlog {} correlationId {}", blogDTO.getName(), correlationId);
    log.trace("POST - addBlog {} correlationId {}", blogDTO, correlationId);
    return blogService.getBlogOrCreate(blogDTO, correlationId)
        .doOnSuccess(blog -> log.debug("END addBlog {} - correlationId {}", blogDTO.getName(), correlationId))
        .doOnError(error -> log.error("ERROR addBlog {} - correlationId {}", blogDTO.getName(), correlationId, error));
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Sluzy do usuwania bloga o podanym id")
  @SwaggerDocumented
  public Mono<Void> deleteBlog(@PathVariable("id") String blogId) {
    String correlationId = UUID.randomUUID().toString();
    log.debug("DELETE - deleteBlog id {} correlationId {}", blogId, correlationId);
    return blogService.deleteBlog(blogId, correlationId)
        .doOnSuccess(blog -> log.debug("END deleteBlog id {} - correlationId {}", blogId, correlationId))
        .doOnError(error -> log.error("ERROR deleteBlog id {} - correlationId {}", blogId, correlationId, error));
  }

  @PostMapping(value = "/evictCache")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void evictCache() {
    log.debug("POST - evict Cache");
    blogService.evictBlogCache();
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
