package pl.michal.olszewski.rssaggregator.item;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.data.mongodb.core.MongoTemplate;

public class ItemListFactory {

  private final MongoTemplate mongoTemplate;

  public ItemListFactory(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public void buildNumberOfItemsAndSave(int numberOfItems, String blogId) {

    List<Item> itemList = IntStream.rangeClosed(1, numberOfItems)
        .parallel()
        .mapToObj(number -> new Item(ItemDTO.builder().link("link" + number).blogId(blogId).title("title" + number).build()))
        .collect(Collectors.toList());
    mongoTemplate.insert(itemList, "item");
  }
}
