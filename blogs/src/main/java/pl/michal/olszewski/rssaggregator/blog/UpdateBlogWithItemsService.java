package pl.michal.olszewski.rssaggregator.blog;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.item.BlogItemLink;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;
import pl.michal.olszewski.rssaggregator.ogtags.OgTagInfoUpdater;

@Service
public class UpdateBlogWithItemsService {

  private static final Logger log = LoggerFactory.getLogger(UpdateBlogWithItemsService.class);

  private final BlogWorker blogUpdater;
  private final Cache<String, BlogDTO> blogCache;
  private final Cache<BlogItemLink, ItemDTO> itemCache;
  private final NewItemInBlogEventProducer producer;
  private final OgTagInfoUpdater ogTagInfoUpdater;

  UpdateBlogWithItemsService(
      BlogWorker blogUpdater,
      @Qualifier("blogCache") Cache<String, BlogDTO> blogCache,
      @Qualifier("itemCache") Cache<BlogItemLink, ItemDTO> itemCache,
      NewItemInBlogEventProducer producer,
      OgTagInfoUpdater ogTagInfoUpdater
  ) {
    this.blogUpdater = blogUpdater;
    this.blogCache = blogCache;
    this.itemCache = itemCache;
    this.producer = producer;
    this.ogTagInfoUpdater = ogTagInfoUpdater;
  }

  public boolean updateBlog(Blog blogFromDb, UpdateBlogWithItemsDTO blogInfoFromRSS) {
    log.debug("aktualizuje bloga {}", blogFromDb.getName());
    blogInfoFromRSS.getItemsList()
        .forEach(item -> addItemToBlog(blogFromDb, item));
    Blog blog = blogUpdater.updateBlogFromDTO(blogFromDb, blogInfoFromRSS.toUpdateBlogDto());
    putToCache(blog);
    return true;
  }

  private void addItemToBlog(Blog blog, ItemDTO item) {
    BlogItemLink itemLink = new BlogItemLink(blog.getId(), item.getLink());
    if (itemCache.getIfPresent(itemLink) == null) {
      log.debug("addItemToBlog {} to blog {}", item.getLink(), blog.getBlogURL());
      item = ogTagInfoUpdater.updateItemByOgTagInfo(item);
      itemCache.put(itemLink, item);
      producer.writeEventToQueue(new NewItemInBlogEvent(item));
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
            updatedBlog.getPublishedDate(),
            updatedBlog.getImageUrl())
    );
  }

}
