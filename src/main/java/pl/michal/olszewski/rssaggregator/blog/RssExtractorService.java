package pl.michal.olszewski.rssaggregator.blog;

import static pl.michal.olszewski.rssaggregator.blog.BlogItemsFromFeedExtractor.getItemsForBlog;

import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.FetcherException;
import com.rometools.fetcher.impl.HttpClientFeedFetcher;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.config.FeedFetcherCache;

@Service
@Slf4j
class RssExtractorService {

  private final FeedFetcherCache feedFetcherCache;

  RssExtractorService(FeedFetcherCache feedFetcherCache) {
    this.feedFetcherCache = feedFetcherCache;
  }

  private BlogDTO getBlogInfo(SyndFeed syndFeed, String feedURL, String blogURL) {
    log.trace("getBlogInfo feedURL {} blogURL {}", feedURL, blogURL);
    return new BlogDTO(
        syndFeed.getLink() != null ? syndFeed.getLink() : blogURL,
        syndFeed.getDescription(),
        syndFeed.getTitle(),
        feedURL,
        syndFeed.getPublishedDate() != null ? syndFeed.getPublishedDate().toInstant() : Instant.now(),
        new ArrayList<>());
  }

  BlogDTO getBlog(Blog.RssInfo info, String correlationID) {
    log.trace("getBlog START {} correlationID {}", info, correlationID);
    try {
      FeedFetcher feedFetcher = new HttpClientFeedFetcher(feedFetcherCache);
      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
      SSLContext.setDefault(ctx);
      URL url = new URL(info.getFeedURL());
      SyndFeed feed = feedFetcher.retrieveFeed("MyAgent", url);
      feed.setEncoding("UTF-8");
      feed.getEntries()
          .parallelStream()
          .filter(v -> v.getPublishedDate() == null && v.getUpdatedDate() != null)
          .forEach(v -> v.setPublishedDate(v.getUpdatedDate()));
      BlogDTO blogInfo = getBlogInfo(feed, info.getFeedURL(), info.getBlogURL());
      getItemsForBlog(feed, info.getLastUpdateDate())
          .forEach(blogInfo::addNewItem);
      log.trace("getBlog STOP {} correlationID {}", info, correlationID);
      return blogInfo;
    } catch (IOException | FeedException | FetcherException | NoSuchAlgorithmException | KeyManagementException e) {
      log.error("wystapił bład przy pobieraniu bloga {} correlationID {}", info, correlationID, e);
      throw new RssException(info.getFeedURL(), correlationID, e);
    }
  }

  static class DefaultTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }
  }
}
