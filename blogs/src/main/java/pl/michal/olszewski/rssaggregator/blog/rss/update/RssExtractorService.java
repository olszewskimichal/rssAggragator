package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static pl.michal.olszewski.rssaggregator.blog.rss.update.BlogItemsFromFeedExtractor.getItemsForBlog;

import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.FetcherException;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.RssInfo;
import pl.michal.olszewski.rssaggregator.blog.UpdateBlogWithItemsDTO;
import pl.michal.olszewski.rssaggregator.blog.UpdateBlogWithItemsDTOBuilder;

@Service
class RssExtractorService {

  private static final Logger log = LoggerFactory.getLogger(RssExtractorService.class);
  private final FeedFetcher feedFetcher;

  RssExtractorService(FeedFetcher feedFetcher) {
    this.feedFetcher = feedFetcher;
  }

  UpdateBlogWithItemsDTO getItemsFromRss(RssInfo info) {
    log.trace("getBlog START {}", info);
    try {
      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
      SSLContext.setDefault(ctx);
      URL url = new URL(info.getFeedURL());
      SyndFeed feed = feedFetcher.retrieveFeed("MyAgent", url);
      feed.setEncoding("UTF-8");
      feed.getEntries()
          .parallelStream()
          .filter(entry -> entry.getPublishedDate() == null && entry.getUpdatedDate() != null)
          .forEach(entry -> entry.setPublishedDate(entry.getUpdatedDate()));
      UpdateBlogWithItemsDTO blogInfo = getBlogInfo(feed, info.getFeedURL(), info.getBlogURL());
      getItemsForBlog(feed, info)
          .forEach(blogInfo::addNewItem);
      log.trace("getBlog STOP {}", info);
      return blogInfo;
    } catch (IOException | FeedException | FetcherException | NoSuchAlgorithmException | KeyManagementException ex) {
      throw new RssException(info.getFeedURL(), ex);
    }
  }

  private UpdateBlogWithItemsDTO getBlogInfo(SyndFeed syndFeed, String feedURL, String blogURL) {
    log.trace("getBlogInfo feedURL {} blogURL {}", feedURL, blogURL);
    return new UpdateBlogWithItemsDTOBuilder().link(syndFeed.getLink() != null ? syndFeed.getLink() : blogURL).description(HtmlTagRemover.removeHtmlTagFromDescription(syndFeed.getDescription()))
        .name(syndFeed.getTitle()).feedURL(feedURL).publishedDate(syndFeed.getPublishedDate() != null ? syndFeed.getPublishedDate().toInstant() : Instant.now()).itemsList(new ArrayList<>())
        .build();
  }

}
