package pl.michal.olszewski.rssaggregator.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.rometools.fetcher.impl.SyndFeedInfo;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
class FeedFetcherCacheImpl implements com.rometools.fetcher.impl.FeedFetcherCache {

  private static final Logger log = LoggerFactory.getLogger(FeedFetcherCacheImpl.class);

  private final Cache<String, SyndFeedInfo> feedCacheProd;

  private FeedFetcherCacheImpl(@Qualifier("feedCache") Cache<String, SyndFeedInfo> feedCacheProd) {
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
