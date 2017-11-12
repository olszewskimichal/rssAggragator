package pl.michal.olszewski.rssaggregator.factory;

import java.util.stream.IntStream;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

public class ItemListFactory {

  private final BlogRepository repository;

  public ItemListFactory(BlogRepository repository) {
    this.repository = repository;
  }

  public void buildNumberOfItemsAndSave(int numberOfItems) {
    Blog blog = new Blog("blog997", "", "", "", null);
    IntStream.rangeClosed(1, numberOfItems).parallel().forEachOrdered(v -> blog.addItem(new Item(ItemDTO.builder().title("title" + v).build())));
    repository.save(blog);
  }
}
