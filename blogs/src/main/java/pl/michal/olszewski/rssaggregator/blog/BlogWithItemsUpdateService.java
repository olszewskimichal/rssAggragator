package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class BlogWithItemsUpdateService {

  private final BlogUpdater blogUpdater;
  private final Cache<String, BlogDTO> blogCache;
  private final Cache<String, ItemDTO> itemCache;
  private final NewItemInBlogEventProducer producer;
  private final NewItemForSearchEventProducer itemForSearchEventProducer;

  public BlogWithItemsUpdateService(
      BlogUpdater blogUpdater,
      @Qualifier("blogCache") Cache<String, BlogDTO> blogCache,
      @Qualifier("itemCache") Cache<String, ItemDTO> itemCache,
      NewItemInBlogEventProducer producer,
      NewItemForSearchEventProducer itemForSearchEventProducer) {
    this.blogUpdater = blogUpdater;
    this.blogCache = blogCache;
    this.itemCache = itemCache;
    this.producer = producer;
    this.itemForSearchEventProducer = itemForSearchEventProducer;
  }

  public Mono<Blog> updateBlog(Blog blogFromDb, UpdateBlogWithItemsDTO blogInfoFromRSS) {
    log.debug("aktualizuje bloga {}", blogFromDb.getName());
    blogInfoFromRSS.getItemsList()
        .forEach(item -> addItemToBlog(blogFromDb, item));
    return blogUpdater.updateBlogFromDTO(blogFromDb, blogInfoFromRSS.toUpdateBlogDto())
        .doOnNext(this::putToCache);
  }

  private void addItemToBlog(Blog blog, ItemDTO item) {
    if (itemCache.getIfPresent(item.getLink()) == null) {
      itemCache.put(item.getLink(), item);
      producer.writeEventToQueue(new NewItemInBlogEvent(Instant.now(), item, blog.getId()));
      itemForSearchEventProducer.writeEventToQueue(new NewItemForSearchEvent(Instant.now(), item.getLink(), item.getTitle(), item.getDescription()));
    }
  }

  private void putToCache(Blog updatedBlog) {
    blogCache.put(
        updatedBlog.getId(),
        new BlogDTO(
            updatedBlog.getId(),
            updatedBlog.getBlogURL(),
            updatedBlog.getDescription(),
            updatedBlog.getName(),
            updatedBlog.getFeedURL(),
            updatedBlog.getPublishedDate()
        )
    );
  }

}
