package pl.michal.olszewski.rssaggregator.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.service.BlogService;

import java.util.List;
import java.util.Optional;

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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BlogDTO> getBlogs(@RequestParam(value = "limit", required = false) Integer limit, @RequestParam(value = "page", required = false) Integer page) {
        return blogService.getAllBlogDTOs(limit, page);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBlog(@RequestBody BlogDTO blogDTO) {
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

}
