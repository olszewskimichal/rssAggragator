package pl.michal.olszewski.rssaggregator.item;

import java.time.Instant;
import java.util.List;

interface CustomItemRepository {

  List<Item> findItemsFromDateOrderByCreatedAt(Instant from);

  PageBlogItemDTO getBlogItemsForBlog(String blogId, Integer limit, Integer page);
}
