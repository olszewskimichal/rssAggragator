package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics.monitor;

import com.rometools.fetcher.FeedFetcher;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogFinder;
import pl.michal.olszewski.rssaggregator.blog.UpdateBlogWithItemsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
class UpdateBlogService {

  private static final Logger log = LoggerFactory.getLogger(UpdateBlogService.class);
  private final BlogFinder blogFinder;
  private final Executor executor;
  private final RssExtractorService rssExtractorService;
  private final UpdateBlogWithItemsService blogService;

  public UpdateBlogService(
      BlogFinder blogFinder,
      Executor executor,
      MeterRegistry registry,
      UpdateBlogWithItemsService blogService,
      FeedFetcher feedFetcher
  ) {
    this.blogFinder = blogFinder;
    this.rssExtractorService = new RssExtractorService(feedFetcher);
    this.blogService = blogService;
    if (registry != null) {
      this.executor = monitor(registry, executor, "prod_pool");
    } else {
      this.executor = executor;
    }
  }

  Flux<Boolean> updateAllActiveBlogsByRss() {
    log.debug("zaczynam aktualizacje blogów ");
    return Flux.merge(getUpdateBlogByRssList())
        .collectList()
        .flatMapMany(Flux::fromIterable)
        .flatMapIterable(list -> list)
        .doOnError(ex ->
            log.error("Aktualizacja zakonczona bledem ", ex)
        );
  }

  Mono<Boolean> updateRssBlogItems(Blog blog) {
    log.debug("Pobieranie nowych danych dla bloga {}", blog.getName());
    return Mono.defer(() -> extractBlogFromRssAndUpdateBlog(blog))
        .timeout(Duration.ofSeconds(5), Mono.error(new UpdateTimeoutException(blog.getName())))
        .doOnError(ex -> log.warn("Nie powiodlo sie pobieranie nowych danych dla bloga {}", blog.getName(), ex))
        .subscribeOn(Schedulers.fromExecutor(executor))
        .onErrorReturn(false);
  }

  private Mono<List<Boolean>> getUpdateBlogByRssList() {
    return blogFinder.findAll()
        .flatMap(this::updateRssBlogItems)
        .collectList();
  }

  private Mono<Boolean> extractBlogFromRssAndUpdateBlog(Blog blog) {
    log.trace("START updateRssBlogItems dla blog {}", blog.getName());
    var blogDTO = rssExtractorService.getBlog(blog.getRssInfo());
    return blogService.updateBlog(blog, blogDTO)
        .doOnSuccess(updatedBlog -> log.trace("STOP updateRssBlogItems dla blog {}", updatedBlog.getName()))
        .map(updatedBlog -> true)
        .onErrorReturn(false);
  }
}
