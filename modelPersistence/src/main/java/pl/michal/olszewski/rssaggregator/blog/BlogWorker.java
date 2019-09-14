package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class BlogWorker {

  private final BlogReactiveRepository repository;

  public BlogWorker(BlogReactiveRepository repository) {
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

  public Mono<Blog> createNewBlog(CreateBlogDTO blogDTO) {
    return repository.save(new Blog(blogDTO));
  }

  Mono<Blog> updateBlogFromDTO(Blog blog, UpdateBlogDTO blogDTO) {
    blog.updateFromDto(blogDTO);
    return repository.save(blog);
  }

  Mono<Void> deleteBlogById(String blogId) {
    return repository.deleteById(blogId);
  }
}
