package pl.michal.olszewski.rssaggregator.blog;

import java.util.UUID;
import java.util.stream.IntStream;

class BlogListFactory {

  private final BlogRepository repository;

  BlogListFactory(BlogRepository repository) {
    this.repository = repository;
  }

  Blog createAndSaveNewBlog() {
    Blog blog = new BlogBuilder()
        .id(UUID.randomUUID().toString())
        .name(UUID.randomUUID().toString())
        .build();
    return repository.save(blog);
  }

  void buildNumberOfBlogsAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs)
        .parallel()
        .mapToObj(number -> new BlogBuilder().blogURL("blog" + number).feedURL("blog" + number).build())
        .forEach(repository::save);
  }

  Blog withURL(String url) {
    return repository.save(new BlogBuilder().blogURL(url).feedURL(url).build());
  }

  void notActive() {
    Blog blog = new BlogBuilder().blogURL("test").feedURL("test").build();
    blog.deactivate();
    repository.save(blog);
  }

  void withName(String name) {
    repository.save(new BlogBuilder().blogURL(name).name(name).build());
  }
}
