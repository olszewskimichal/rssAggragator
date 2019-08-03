package pl.michal.olszewski.rssaggregator.blog;

import java.util.List;
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

  void buildNumberOfBlogsDTOAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs)
        .parallel()
        .mapToObj(number -> Blog.builder().blogURL("blog" + number).build())
        .map(repository::save)
        .forEach(Mono::block);
  }

  Blog createAndSaveNewBlog() {
    Blog blog = Blog.builder()
        .id(UUID.randomUUID().toString())
        .name(UUID.randomUUID().toString())
        .build();
    return repository.save(blog).block();
  }

  List<Blog> buildNumberOfBlogsAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs)
        .parallel()
        .mapToObj(number -> Blog.builder().blogURL("blog" + number).feedURL("blog" + number).build())
        .map(repository::save)
        .forEach(Mono::block);
    return repository.findAll()
        .collectList()
        .block();

  }

  Blog withURL(String url) {
    return repository.save(Blog.builder().blogURL(url).feedURL(url).build()).block();
  }

  void notActive() {
    Blog blog = Blog.builder().blogURL("test").feedURL("test").build();
    blog.deactivate();
    repository.save(blog).block();
  }

  void withName(String name) {
    repository.save(Blog.builder().blogURL(name).name(name).build()).block();
  }
}
