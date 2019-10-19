package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class BlogWorker {

  private final BlogRepository repository;

  public BlogWorker(BlogRepository repository) {
    this.repository = repository;
  }

  public Mono<Blog> activateBlog(Blog blog) {
    blog.activate();
    return Mono.just(repository.save(blog));
  }

  public Mono<Blog> deactivateBlog(Blog blog) {
    blog.deactivate();
    return Mono.just(repository.save(blog));
  }

  public Mono<Blog> createNewBlog(CreateBlogDTO blogDTO) {
    return Mono.just(repository.save(new Blog(blogDTO)));
  }

  Mono<Blog> updateBlogFromDTO(Blog blog, UpdateBlogDTO blogDTO) {
    blog.updateFromDto(blogDTO);
    return Mono.just(repository.save(blog));
  }

  void deleteBlogById(String blogId) {
    repository.deleteById(blogId);
  }
}
