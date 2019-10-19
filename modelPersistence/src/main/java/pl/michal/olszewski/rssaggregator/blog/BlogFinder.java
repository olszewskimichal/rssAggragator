package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BlogFinder {

  private final BlogRepository blogRepository;

  BlogFinder(BlogRepository blogRepository) {
    this.blogRepository = blogRepository;
  }

  public List<Blog> findAll() {
    return blogRepository.findAll();
  }

  public Optional<BlogAggregationDTO> getBlogWithCount(String id) {
    return blogRepository.getBlogWithCount(id);
  }

  Optional<Blog> findByFeedURL(String url) {
    return blogRepository.findByFeedURL(url);
  }

  Optional<Blog> findById(String id) {
    return blogRepository.findById(id);
  }
}
