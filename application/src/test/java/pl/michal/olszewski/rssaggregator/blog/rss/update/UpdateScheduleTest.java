package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.benmanes.caffeine.cache.Cache;
import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.FetcherException;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jms.core.JmsTemplate;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogAggregationDTO;
import pl.michal.olszewski.rssaggregator.blog.BlogBuilder;
import pl.michal.olszewski.rssaggregator.blog.BlogFinder;
import pl.michal.olszewski.rssaggregator.extenstions.TimeExecutionLogger;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class UpdateScheduleTest extends IntegrationTestBase implements TimeExecutionLogger {

  @Autowired
  private UpdateBlogService updateBlogService;

  @Autowired
  private BlogFinder blogRepository;

  @Autowired
  @Qualifier(value = "blogCache")
  private Cache blogCache;

  @Autowired
  private MongoTemplate mongoTemplate;

  @MockBean
  private FeedFetcher feedFetcher;

  @MockBean
  private JmsTemplate jmsTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "blog");
    blogCache.invalidateAll();
  }

  @Test
  void shouldUpdateBlog() throws IOException, FetcherException, FeedException {
    SyndFeedImpl syndFeed = buildSyndFeed();
    given(feedFetcher.retrieveFeed(anyString(), any())).willReturn(syndFeed);

    Blog blog = new BlogBuilder()
        .blogURL("https://spring.io/")
        .name("spring")
        .feedURL("https://spring.blog.test/")
        .build();
    mongoTemplate.save(blog);

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(true)
        .expectComplete()
        .verify();

    Mono<BlogAggregationDTO> updatedBlog = blogRepository.getBlogWithCount(blog.getId());
    StepVerifier
        .create(updatedBlog)
        .expectNextCount(1L)
        .expectComplete()
        .verify();
    verify(jmsTemplate, times(2)).convertAndSend(anyString(), any(NewItemForSearchEvent.class));
    verify(jmsTemplate, times(2)).convertAndSend(anyString(), any(NewItemInBlogEvent.class));

  }

  @Test
  void shouldNotUpdateBlogWhenLastUpdatedDateIsAfterPublishedItems() throws IOException, FetcherException, FeedException {
    SyndFeedImpl syndFeed = buildSyndFeed();

    given(feedFetcher.retrieveFeed(anyString(), any())).willReturn(syndFeed);

    Blog blog = new BlogBuilder()
        .blogURL("https://devstyle.pl")
        .name("devstyle.pl")
        .feedURL("https://devstyle.pl/feed")
        .lastUpdateDate(Instant.now())
        .build();
    mongoTemplate.save(blog);

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(true)
        .expectComplete()
        .verify();

    Mono<BlogAggregationDTO> updatedBlog = blogRepository.getBlogWithCount(blog.getId());
    StepVerifier
        .create(updatedBlog)
        .assertNext(aggregationDTO -> assertThat(aggregationDTO.getBlogItemsCount()).isEqualTo(0))
        .expectComplete()
        .verify();
    verify(jmsTemplate, times(0)).convertAndSend(anyString(), any(NewItemForSearchEvent.class));
    verify(jmsTemplate, times(0)).convertAndSend(anyString(), any(NewItemInBlogEvent.class));

  }

  @Test
  void shouldReturnFalseOnTimeout() {
    Blog blog = new BlogBuilder()
        .blogURL("https://spring.io/")
        .name("spring")
        .feedURL("https://xcasdasda.io/")
        .build();
    mongoTemplate.save(blog);

    Mono<Boolean> result = updateBlogService.updateRssBlogItems(blog);
    StepVerifier.withVirtualTime(() -> result)
        .thenAwait(Duration.ofSeconds(5))
        .expectNext(false)
        .verifyComplete();
  }

  @Test
  void shouldReturnFalseWhenFetcherFailed() throws FetcherException, IOException, FeedException {
    given(feedFetcher.retrieveFeed(anyString(), any())).willThrow(new FeedException("some exception"));

    Blog blog = new BlogBuilder()
        .blogURL("https://devstyle.pl")
        .name("devstyle.pl")
        .feedURL("https://devstyle.xxx/feed")
        .build();

    mongoTemplate.save(blog);

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(false)
        .verifyComplete();
  }

  private SyndFeedImpl buildSyndFeed() {
    SyndFeedImpl syndFeed = new SyndFeedImpl();
    SyndEntryImpl syndEntryWithDescription = new SyndEntryImpl();
    SyndContentImpl syndContent = new SyndContentImpl();
    syndContent.setValue("<html>tekst</html>");
    syndEntryWithDescription.setDescription(syndContent);
    syndEntryWithDescription.setPublishedDate(new Date());
    syndEntryWithDescription.setLink("link");
    SyndEntryImpl syndEntryWithoutDescription = new SyndEntryImpl();
    syndEntryWithoutDescription.setPublishedDate(new Date());
    syndEntryWithoutDescription.setLink("link2");
    syndFeed.getEntries().add(syndEntryWithDescription);
    syndFeed.getEntries().add(syndEntryWithoutDescription);
    return syndFeed;
  }

}
