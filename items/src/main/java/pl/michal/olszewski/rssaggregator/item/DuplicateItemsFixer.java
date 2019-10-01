package pl.michal.olszewski.rssaggregator.item;

import static java.time.temporal.ChronoUnit.DAYS;
import static pl.michal.olszewski.rssaggregator.item.LinkExtractor.getFinalURL;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class DuplicateItemsFixer {

  private final ItemFinder itemFinder;
  private final MongoTemplate mongoTemplate;
  private final Cache<BlogItemLink, ItemDTO> itemCache;

  public DuplicateItemsFixer(
      ItemFinder itemFinder,
      MongoTemplate mongoTemplate,
      @Qualifier("itemCache") Cache<BlogItemLink, ItemDTO> itemCache
  ) {
    this.itemFinder = itemFinder;
    this.mongoTemplate = mongoTemplate;
    this.itemCache = itemCache;
  }

  @Scheduled(initialDelay = 1000 * 60 * 60 * 6, fixedDelay = 1000 * 60 * 60 * 6)
  void removeDuplicatesFromLastWeek() {
    log.debug("Scheduler started");
    itemFinder.findItemsFromDateOrderByCreatedAt(Instant.now().minus(7, DAYS))
        .forEach(this::removeIfIsDuplicate);
    log.debug("Scheduler finished");
  }

  private void removeIfIsDuplicate(Item item) {
    log.debug("removeIfIsDuplicate from item {}", item);
    String fixedUrl = getFinalURL(item.getLink());
    if (urlHasChanged(item.getLink(), fixedUrl)) {
      try {
        item.updateLink(fixedUrl);
        mongoTemplate.save(item);
      } catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ex) {
        log.error("removeIfIsDuplicate error for item {}", item);
        mongoTemplate.remove(item);
        itemCache.invalidate(new BlogItemLink(item.getBlogId(), item.getLink()));
      }
    }
  }

  private boolean urlHasChanged(String link, String fixedUrl) {
    return !Objects.equals(link, fixedUrl);
  }
}
