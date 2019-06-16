package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.benmanes.caffeine.cache.Cache;
import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.FetcherException;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jms.core.JmsTemplate;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;
import pl.michal.olszewski.rssaggregator.blog.failure.BlogUpdateFailedEvent;
import pl.michal.olszewski.rssaggregator.extenstions.TimeExecutionLogger;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class UpdateScheduleTest extends IntegrationTestBase implements TimeExecutionLogger {

  @Autowired
  private UpdateBlogService updateBlogService;

  @Autowired
  private BlogReactiveRepository blogRepository;

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
    mongoTemplate.remove(new Query(), "item");
    blogRepository.deleteAll().block();
    blogCache.invalidateAll();
  }

  @Test
  void shouldUpdateBlog() throws IOException, FetcherException, FeedException {
    SyndFeedImpl syndFeed = buildSyndFeed();
    given(feedFetcher.retrieveFeed(Mockito.anyString(), Mockito.any())).willReturn(syndFeed);

    Blog blog = Blog.builder()
        .blogURL("https://spring.io/")
        .name("spring")
        .feedURL("https://spring.blog.test/")
        .build();
    blogRepository.save(blog).block();

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(true)
        .expectComplete()
        .verify();

    Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());
    StepVerifier
        .create(updatedBlog)
        .assertNext(v -> assertThat(v.getItems()).isNotNull().isNotEmpty())
        .expectComplete()
        .verify();
  }

  @Test
  void shouldNotUpdateBlogWhenLastUpdatedDateIsAfterPublishedItems() throws IOException, FetcherException, FeedException {
    SyndFeedImpl syndFeed = buildSyndFeed();

    given(feedFetcher.retrieveFeed(Mockito.anyString(), Mockito.any())).willReturn(syndFeed);

    Blog blog = Blog.builder()
        .blogURL("https://devstyle.pl")
        .name("devstyle.pl")
        .feedURL("https://devstyle.pl/feed")
        .lastUpdateDate(Instant.now())
        .build();
    blogRepository.save(blog).block();

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(true)
        .expectComplete()
        .verify();

    Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());
    StepVerifier
        .create(updatedBlog)
        .assertNext(v -> assertThat(v.getItems()).hasSize(0))
        .expectComplete()
        .verify();
  }

 /* @Test
  void shouldUpdateBlogWithNotValidCertification() {
    Blog blog = Blog.builder()
        .blogURL("https://koziolekweb.pl")
        .name("koziolekweb.pl")
        .feedURL("https://koziolekweb.pl/feed/")
        .lastUpdateDate(Instant.now())
        .build();
    blogRepository.save(blog).block();

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(true)
        .expectComplete()
        .verify();
  }*/

  @Test
  void shouldReturnFalseOnTimeoutAndWriteNewEventToDB() {
    Blog blog = Blog.builder()
        .blogURL("https://spring.io/")
        .name("spring")
        .feedURL("https://xcasdasda.io/")
        .build();
    blogRepository.save(blog).block();

    Mono<Boolean> result = updateBlogService.updateRssBlogItems(blog);
    StepVerifier.withVirtualTime(() -> result)
        .thenAwait(Duration.ofSeconds(5))
        .expectNext(false)
        .verifyComplete();
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(BlogUpdateFailedEvent.class));
  }

  @Test
  void shouldWriteNewEventToDBWhenFetcherFailed() throws FetcherException, IOException, FeedException {
    given(feedFetcher.retrieveFeed(Mockito.anyString(), Mockito.any())).willThrow(new FeedException("some exception"));

    Blog blog = Blog.builder()
        .blogURL("https://devstyle.pl")
        .name("devstyle.pl")
        .feedURL("https://devstyle.xxx/feed")
        .build();

    blogRepository.save(blog).block();

    Flux<Boolean> result = updateBlogService.updateAllActiveBlogsByRss();

    StepVerifier
        .create(result)
        .expectNext(false)
        .verifyComplete();
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(BlogUpdateFailedEvent.class));
  }

  private SyndFeedImpl buildSyndFeed() {
    SyndFeedImpl syndFeed = new SyndFeedImpl();
    SyndEntryImpl syndEntry = new SyndEntryImpl();
    syndEntry.setPublishedDate(new Date());
    syndEntry.setLink("link");
    syndFeed.getEntries().add(syndEntry);
    return syndFeed;
  }

}
