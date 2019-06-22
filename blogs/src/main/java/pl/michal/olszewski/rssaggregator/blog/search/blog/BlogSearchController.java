package pl.michal.olszewski.rssaggregator.blog.search.blog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/blogs/search")
public class BlogSearchController {

  private final BlogTextSearchRepository blogTextSearchRepository;

  public BlogSearchController(BlogTextSearchRepository blogTextSearchRepository) {
    this.blogTextSearchRepository = blogTextSearchRepository;
  }

  @GetMapping
  Flux<BlogSearchResult> searchItemsMatchingBy(
      @RequestParam(value = "text") String searchText,
      @RequestParam(value = "limit", required = false) Integer limit) {
    return blogTextSearchRepository.findMatching(
        searchText,
        limit == null ? 10 : limit
    );
  }
}
