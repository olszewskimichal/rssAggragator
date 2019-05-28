package pl.michal.olszewski.rssaggregator.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.rometools.fetcher.impl.SyndFeedInfo;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeedFetcherCache implements com.rometools.fetcher.impl.FeedFetcherCache {

  private final Cache<String, SyndFeedInfo> feedCacheProd;

  public FeedFetcherCache(@Qualifier("feedCache") Cache<String, SyndFeedInfo> feedCacheProd) {
    this.feedCacheProd = feedCacheProd;
  }

  @Override
  public SyndFeedInfo getFeedInfo(URL url) {
    log.info("GetFeedInfo from cache {}", url);
    return feedCacheProd.getIfPresent(url.toString());
  }

  @Override
  public void setFeedInfo(URL url, SyndFeedInfo syndFeedInfo) {
    log.info("setFeedInfo in cache for URL {}", url);
    feedCacheProd.put(url.toString(), syndFeedInfo);
  }

  @Override
  public void clear() {
    log.info("CleanUp cache");
    feedCacheProd.cleanUp();
  }

  @Override
  public SyndFeedInfo remove(URL url) {
    log.info("Remove from cache {}", url);
    SyndFeedInfo syndFeedInfo = getFeedInfo(url);
    log.info("Try to invalidate {}", syndFeedInfo.getETag());
    feedCacheProd.invalidate(syndFeedInfo);
    return syndFeedInfo;
  }
}
