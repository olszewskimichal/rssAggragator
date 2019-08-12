package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static pl.michal.olszewski.rssaggregator.blog.rss.update.BlogItemsFromFeedExtractor.getItemsForBlog;

import brave.Tracer;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.RssInfo;
import pl.michal.olszewski.rssaggregator.blog.UpdateBlogWithItemsDTO;

@Service
@Slf4j
class RssExtractorService {

  private final FeedFetcher feedFetcher;
  private final Tracer tracer;

  RssExtractorService(FeedFetcher feedFetcher, Tracer tracer) {
    this.feedFetcher = feedFetcher;
    this.tracer = tracer;
  }

  private UpdateBlogWithItemsDTO getBlogInfo(SyndFeed syndFeed, String feedURL, String blogURL) {
    log.trace("getBlogInfo feedURL {} blogURL {}", feedURL, blogURL);
    return new UpdateBlogWithItemsDTO(
        syndFeed.getLink() != null ? syndFeed.getLink() : blogURL,
        HtmlTagRemover.removeHtmlTagFromDescription(syndFeed.getDescription()),
        syndFeed.getTitle(),
        feedURL,
        syndFeed.getPublishedDate() != null ? syndFeed.getPublishedDate().toInstant() : Instant.now(),
        new ArrayList<>());
  }

  UpdateBlogWithItemsDTO getBlog(RssInfo info) {
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

}
