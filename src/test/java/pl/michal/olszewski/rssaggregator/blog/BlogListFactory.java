package pl.michal.olszewski.rssaggregator.blog;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.michal.olszewski.rssaggregator.item.Item;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import reactor.core.publisher.Mono;

//TODO przerobic calosc
@Slf4j
class BlogListFactory {

  private final BlogReactiveRepository repository;
  private final MongoTemplate itemRepository;

  BlogListFactory(BlogReactiveRepository repository, MongoTemplate itemRepository) {
    this.repository = repository;
    this.itemRepository = itemRepository;
  }

  List<BlogDTO> buildNumberOfBlogsDTOAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs).parallel().mapToObj(number -> new Blog("blog" + number, "", "", "", null, null)).
        map(repository::save).forEach(Mono::block);
    return repository.findAll().map(v -> new BlogDTO(v.getBlogURL(), v.getDescription(), v.getName(), v.getFeedURL(), v.getPublishedDate(), Collections.emptyList()))
        .collectList().block();
  }

  Blog buildBlogWithItemsAndSave(int numberOfItems) {
    Blog blog = new Blog("blog997", "", "", "", null, null);
    IntStream.rangeClosed(1, numberOfItems).parallel().forEachOrdered(v -> blog.addItem(new Item(ItemDTO.builder().link("link" + v).title("title" + v).build()), itemRepository));
    log.debug("Zapisuje do bazy blog {}", blog);
    return repository.save(blog).block();
  }

  List<Blog> buildNumberOfBlogsAndSave(int numberOfBlogs) {
    IntStream.range(0, numberOfBlogs).parallel().mapToObj(number -> new Blog("blog" + number, "", "blog", "", null, null)).
        map(repository::save).forEach(Mono::block);
    return repository.findAll()
        .collectList()
        .block();

  }

  Blog withURL(String url) {
    return repository.save(new Blog(url, "", "", url, null, null)).block();
  }

  Blog notActive() {
    Blog blog = new Blog("test", "", "", "test", null, null);
    blog.deactive();
    return repository.save(blog).block();
  }

  Blog withName(String name) {
    return repository.save(new Blog(name, "", name, "", null, null)).block();
  }
}
