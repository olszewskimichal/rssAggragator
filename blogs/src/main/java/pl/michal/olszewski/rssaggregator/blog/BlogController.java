package pl.michal.olszewski.rssaggregator.blog;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import pl.michal.olszewski.rssaggregator.config.SwaggerDocumented;

@RestController
@RequestMapping("/api/v1/blogs")
@CrossOrigin
class BlogController {

  private static final Logger log = LoggerFactory.getLogger(BlogController.class);
  private final BlogService blogService;

  public BlogController(BlogService blogService) {
    this.blogService = blogService;
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Sluzy do pobierania informacji na temat bloga o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Zwraca informacje na temat bloga", response = BlogDTO.class),
      @ApiResponse(code = 404, message = "Blog o podanym id nie istnieje"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public BlogDTO getBlog(@PathVariable("id") String blogId) {
    log.debug("START GET blog by id {}", blogId);
    return blogService.getBlogDTOById(blogId);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Sluzy do pobierania informacji na temat wszystkich blogow")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Zwraca informacje na temat blogow", response = BlogDTO.class),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public PageBlogDTO getBlogs(
      @ApiParam(name = "limit")
      @RequestParam(value = "limit", required = false) Integer limit,
      @ApiParam(name = "page")
      @RequestParam(value = "page", required = false) Integer page
  ) {
    log.debug("START GET blogs");
    return blogService.getAllBlogDTOs(limit, page);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Sluzy do aktualizacji bloga o podanym id")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Zwraca informacje na temat bloga", response = BlogDTO.class),
      @ApiResponse(code = 404, message = "Blog o podanym id nie istnieje"),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public BlogDTO updateBlog(@RequestBody UpdateBlogDTO blogDTO, @PathVariable String id) {
    log.debug("PUT - updateBlog {} {}", id, blogDTO);
    return blogService.updateBlog(blogDTO, id);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Sluzy do dodawania nowego bloga")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Zwraca informacje na temat bloga", response = BlogDTO.class),
      @ApiResponse(code = 500, message = "Internal server error")
  })
  @SwaggerDocumented
  public BlogDTO addBlog(@RequestBody CreateBlogDTO blogDTO) {
    log.debug("POST - addBlog {}", blogDTO);
    return blogService.getBlogOrCreate(blogDTO);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Sluzy do usuwania bloga o podanym id")
  @SwaggerDocumented
  public void deleteBlog(@PathVariable("id") String blogId) {
    log.debug("DELETE - deleteBlog id {}", blogId);
    blogService.deleteBlog(blogId);
    log.debug("END deleteBlog id {}", blogId);
  }
}
