package pl.michal.olszewski.rssaggregator.blog.items;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
class BlogItemsService {

  private final BlogReactiveRepository blogRepository;

  BlogItemsService(BlogReactiveRepository blogRepository) {
    this.blogRepository = blogRepository;
  }

  Flux<BlogItemDTO> getBlogItemsForBlog(String blogId, String correlationId) {
    log.debug("getBlogItemsForBlog {} correlationId {}", blogId, correlationId);
    return blogRepository.findById(blogId)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(blogId, correlationId)))
        .flatMapIterable(this::extractItems);
  }

  private List<BlogItemDTO> extractItems(Blog blog) {
    return blog.getItems().stream()
        .parallel()
        .map(BlogItemDTO::new)
        .collect(Collectors.toList());
  }
}
