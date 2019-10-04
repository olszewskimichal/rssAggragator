package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;
import reactor.core.publisher.Mono;

@Service
public class UpdateBlogWithItemsService {

  private static final Logger log = LoggerFactory.getLogger(UpdateBlogWithItemsService.class);

  private final BlogWorker blogUpdater;
  private final Cache<String, BlogDTO> blogCache;
  private final Cache<String, ItemDTO> itemCache;
  private final NewItemInBlogEventProducer producer;
  private final NewItemForSearchEventProducer itemForSearchEventProducer;

  UpdateBlogWithItemsService(
      BlogWorker blogUpdater,
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
      producer.writeEventToQueue(new NewItemInBlogEvent(item, blog.getId()));
      itemForSearchEventProducer.writeEventToQueue(new NewItemForSearchEvent(item.getLink(), item.getTitle(), item.getDescription()));
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
