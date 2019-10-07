package pl.michal.olszewski.rssaggregator.blog;

import java.util.UUID;
import java.util.stream.IntStream;
import reactor.core.publisher.Mono;

class BlogListFactory {

  private final BlogReactiveRepository repository;

  BlogListFactory(BlogReactiveRepository repository) {
    this.repository = repository;
  }

  Blog createAndSaveNewBlog() {
    Blog blog = new BlogBuilder()
        .id(UUID.randomUUID().toString())
        .name(UUID.randomUUID().toString())
        .build();
    return repository.save(blog).block();
  }

  void buildNumberOfBlogsAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs)
        .parallel()
        .mapToObj(number -> new BlogBuilder().blogURL("blog" + number).feedURL("blog" + number).build())
        .map(repository::save)
        .forEach(Mono::block);
  }

  Blog withURL(String url) {
    return repository.save(new BlogBuilder().blogURL(url).feedURL(url).build()).block();
  }

  void notActive() {
    Blog blog = new BlogBuilder().blogURL("test").feedURL("test").build();
    blog.deactivate();
    repository.save(blog).block();
  }

  void withName(String name) {
    repository.save(new BlogBuilder().blogURL(name).name(name).build()).block();
  }
}
