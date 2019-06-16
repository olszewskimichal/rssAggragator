package pl.michal.olszewski.rssaggregator.item;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.data.mongodb.core.MongoTemplate;

class ItemListFactory {

  private final MongoTemplate mongoTemplate;

  ItemListFactory(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  void buildNumberOfItemsAndSave(int numberOfItems) {

    List<Item> itemList = IntStream.rangeClosed(1, numberOfItems)
        .parallel()
        .mapToObj(number -> new Item(ItemDTO.builder().link("link" + number).title("title" + number).build()))
        .collect(Collectors.toList());
    mongoTemplate.insert(itemList, "item");
  }
}
