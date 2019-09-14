package pl.michal.olszewski.rssaggregator.item;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static pl.michal.olszewski.rssaggregator.item.ItemDTO.builder;

import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;

public class ItemListFactory {

  private final MongoTemplate mongoTemplate;

  public ItemListFactory(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public void buildNumberOfItemsAndSave(int numberOfItems, String blogId) {

    List<Item> itemList = rangeClosed(1, numberOfItems)
        .parallel()
        .mapToObj(number -> new Item(builder().link("link" + number).blogId(blogId).title("title" + number).build()))
        .collect(toList());
    mongoTemplate.insert(itemList, "item");
  }
}
