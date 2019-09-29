package pl.michal.olszewski.rssaggregator.item;

import static java.time.temporal.ChronoUnit.DAYS;
import static pl.michal.olszewski.rssaggregator.item.LinkExtractor.getFinalURL;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class DuplicateItemsFixer {

  private final ItemFinder itemFinder;
  private final MongoTemplate mongoTemplate;

  public DuplicateItemsFixer(ItemFinder itemFinder, MongoTemplate mongoTemplate) {
    this.itemFinder = itemFinder;
    this.mongoTemplate = mongoTemplate;
  }

  void removeDuplicatesFromLastWeek() {
    Instant from = Instant.now().minus(7, DAYS);
    itemFinder.findItemsFromDateOrderByCreatedAt(from)
        .forEach(this::removeIfIsDuplicate);
  }

  private void removeIfIsDuplicate(Item item) {
    log.debug("removeIfIsDuplicate from item {}", item);
    item.updateLink(getFinalURL(item.getLink()));
    try {
      mongoTemplate.save(item);
    } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ex) {
      log.error("removeIfIsDuplicate error for item {}", item);
      mongoTemplate.remove(item);
    }

  }
}
