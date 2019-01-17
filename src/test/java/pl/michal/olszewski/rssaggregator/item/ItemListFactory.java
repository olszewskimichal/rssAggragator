package pl.michal.olszewski.rssaggregator.item;

import java.util.stream.IntStream;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogRepository;

class ItemListFactory {

  private final BlogRepository repository;

  ItemListFactory(BlogRepository repository) {
    this.repository = repository;
  }

  void buildNumberOfItemsAndSave(int numberOfItems) {
    Blog blog = new Blog("blog997", "", "", "", null, null);
    IntStream.rangeClosed(1, numberOfItems).parallel().forEachOrdered(v -> blog.addItem(new Item(ItemDTO.builder().title("title" + v).build())));
    repository.save(blog);
  }
}
