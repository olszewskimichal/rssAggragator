package pl.michal.olszewski.rssaggregator.item;

import java.util.stream.IntStream;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;

class ItemListFactory {

  private final BlogReactiveRepository repository;
  private final MongoTemplate itemRepository;

  ItemListFactory(BlogReactiveRepository repository, MongoTemplate itemRepository) {
    this.repository = repository;
    this.itemRepository = itemRepository;
  }

  void buildNumberOfItemsAndSave(int numberOfItems) {
    Blog blog = new Blog("blog997", "", "", "", null, null);
    IntStream.rangeClosed(1, numberOfItems).parallel().forEachOrdered(v -> blog.addItem(new Item(ItemDTO.builder().link("link" + v).title("title" + v).build()), itemRepository));
    repository.save(blog).block();
  }
}
