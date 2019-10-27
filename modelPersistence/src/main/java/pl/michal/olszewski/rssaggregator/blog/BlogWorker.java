package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.stereotype.Service;

@Service
class BlogWorker {

  private final BlogRepository repository;

  public BlogWorker(BlogRepository repository) {
    this.repository = repository;
  }

  public Blog activateBlog(Blog blog) {
    blog.activate();
    return repository.save(blog);
  }

  public Blog deactivateBlog(Blog blog) {
    blog.deactivate();
    return repository.save(blog);
  }

  public Blog createNewBlog(CreateBlogDTO blogDTO) {
    return repository.save(new Blog(blogDTO));
  }

  Blog updateBlogFromDTO(Blog blog, UpdateBlogDTO blogDTO) {
    blog.updateFromDto(blogDTO);
    return repository.save(blog);
  }

  void deleteBlogById(String blogId) {
    repository.deleteById(blogId);
  }
}
