package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class BlogUpdater {

  private final BlogReactiveRepository repository;

  public BlogUpdater(BlogReactiveRepository repository) {
    this.repository = repository;
  }

  public Mono<Blog> activateBlog(Blog blog) {
    blog.activate();
    return repository.save(blog);
  }

  public Mono<Blog> deactivateBlog(Blog blog) {
    blog.deactivate();
    return repository.save(blog);
  }

  Mono<Blog> updateBlogFromDTO(Blog blog, BlogDTO blogDTO) {
    blog.updateFromDto(blogDTO);
    return repository.save(blog);
  }

  public Mono<Blog> createNewBlog(BlogDTO blogDTO) {
    return repository.save(new Blog(blogDTO));
  }

  Mono<Void> deleteBlogById(String blogId) {
    return repository.deleteById(blogId);
  }

  public Mono<Void> deleteAll() {
    return repository.deleteAll();
  }
}
