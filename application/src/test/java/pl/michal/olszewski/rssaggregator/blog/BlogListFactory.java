package pl.michal.olszewski.rssaggregator.blog;

import static pl.michal.olszewski.rssaggregator.blog.Blog.builder;

import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
class BlogListFactory {

  private final BlogReactiveRepository repository;

  BlogListFactory(BlogReactiveRepository repository) {
    this.repository = repository;
  }

  Blog createAndSaveNewBlog() {
    Blog blog = builder()
        .id(UUID.randomUUID().toString())
        .name(UUID.randomUUID().toString())
        .build();
    return repository.save(blog).block();
  }

  void buildNumberOfBlogsAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs)
        .parallel()
        .mapToObj(number -> builder().blogURL("blog" + number).feedURL("blog" + number).build())
        .map(repository::save)
        .forEach(Mono::block);
  }

  Blog withURL(String url) {
    return repository.save(builder().blogURL(url).feedURL(url).build()).block();
  }

  void notActive() {
    Blog blog = builder().blogURL("test").feedURL("test").build();
    blog.deactivate();
    repository.save(blog).block();
  }

  void withName(String name) {
    repository.save(builder().blogURL(name).name(name).build()).block();
  }
}
