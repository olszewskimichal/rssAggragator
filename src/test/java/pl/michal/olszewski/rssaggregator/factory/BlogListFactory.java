package pl.michal.olszewski.rssaggregator.factory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

public class BlogListFactory {

  private final BlogRepository repository;

  public BlogListFactory(BlogRepository repository) {
    this.repository = repository;
  }

  public List<BlogDTO> buildNumberOfBlogsDTOAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs).parallel().mapToObj(number -> new Blog("blog" + number, "", "", "", null, null)).
        forEach(repository::save);
    return repository.findAll().stream().map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), Collections.emptyList()))
        .collect(Collectors.toList());
  }

  public Blog buildBlogWithItemsAndSave(int numberOfItems) {
    Blog blog = new Blog("blog997", "", "", "", null, null);
    IntStream.rangeClosed(1, numberOfItems).parallel().forEachOrdered(v -> blog.addItem(new Item(ItemDTO.builder().title("title" + v).build())));
    return repository.save(blog);
  }

  public List<Blog> buildNumberOfBlogsAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs).parallel().mapToObj(number -> new Blog("blog" + number, "", "blog", "", null, null)).
        forEach(repository::save);
    return repository.findAll();

  }

  public Blog withURL(String url) {
    return repository.save(new Blog(url, "", "", url, null, null));
  }

  public Blog notActive() {
    Blog blog = new Blog("test", "", "", "test", null, null);
    blog.deactive();
    return repository.save(blog);
  }

  public Blog withName(String name) {
    return repository.save(new Blog(name, "", name, "", null, null));
  }
}
