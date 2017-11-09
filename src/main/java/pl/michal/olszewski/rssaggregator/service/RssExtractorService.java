package pl.michal.olszewski.rssaggregator.service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.exception.RssException;

@Service
@Slf4j
public class RssExtractorService {

  private static List<ItemDTO> getItemsForBlog(SyndFeed syndFeed) {
    return syndFeed.getEntries().stream()
        .map(entry -> new ItemDTO(
            entry.getTitle(),
            entry.getDescription() != null ? entry.getDescription().getValue() : "",
            entry.getLink(),
            entry.getPublishedDate() == null ? entry.getUpdatedDate().toInstant() : entry.getPublishedDate().toInstant(),
            entry.getAuthor()))
        .collect(Collectors.toList());
  }

  private BlogDTO getBlogInfo(SyndFeed syndFeed, String feedURL, String blogURL) {
    return new BlogDTO(syndFeed.getLink() != null ? syndFeed.getLink() : blogURL, syndFeed.getDescription(), syndFeed.getTitle(), feedURL, syndFeed.getPublishedDate().toInstant(), new ArrayList<>());
  }

  public BlogDTO getBlog(XmlReader xmlReader, String feedURL, String blogURL) {
    try (XmlReader reader = xmlReader) {
      SyndFeed feed = new SyndFeedInput().build(reader);
      BlogDTO blogInfo = getBlogInfo(feed, feedURL, blogURL);
      getItemsForBlog(feed).forEach(blogInfo::addNewItem);
      return blogInfo;
    } catch (IOException | FeedException e) {
      throw new RssException(feedURL);
    }
  }
}
