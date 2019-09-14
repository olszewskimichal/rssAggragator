package pl.michal.olszewski.rssaggregator.config;

import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.impl.HttpClientFeedFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FeedFetcherConfig {

  @Bean
  public FeedFetcher feedFetcher(FeedFetcherCacheImpl feedFetcherCache) {
    return new HttpClientFeedFetcher(feedFetcherCache);
  }

}
