package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics.monitor;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.rometools.fetcher.FeedFetcher;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogFinder;
import pl.michal.olszewski.rssaggregator.blog.UpdateBlogWithItemsService;

@Service
class UpdateBlogService {

  private static final Logger log = LoggerFactory.getLogger(UpdateBlogService.class);
  private final BlogFinder blogFinder;
  private final Executor executor;
  private final RssExtractorService rssExtractorService;
  private final UpdateBlogWithItemsService blogService;

  UpdateBlogService(
      BlogFinder blogFinder,
      Executor executor,
      MeterRegistry registry,
      UpdateBlogWithItemsService blogService,
      FeedFetcher feedFetcher
  ) {
    this.blogFinder = blogFinder;
    this.rssExtractorService = new RssExtractorService(feedFetcher);
    this.blogService = blogService;
    this.executor = monitor(registry, executor, "prod_pool");
  }

  List<Boolean> updateAllBlogs() {
    List<Blog> blogList = blogFinder.findAll().collectList().block();
    List<CompletableFuture<Boolean>> futureList = blogList.stream()
        .map(blog -> getItemsFromRssAndUpdateBlogWithTimeout(blog, 5L))
        .collect(Collectors.toList());
    CompletableFuture.allOf(futureList.toArray(CompletableFuture[]::new)).join();
    return futureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
  }

  CompletableFuture<Boolean> getItemsFromRssAndUpdateBlogWithTimeout(Blog blog, Long timeInSeconds) {
    return CompletableFuture.supplyAsync(() -> rssExtractorService.getItemsFromRss(blog.getRssInfo()), executor)
        .thenApplyAsync(updateBlogWithItemsDTO -> blogService.updateBlogSync(blog, updateBlogWithItemsDTO))
        .orTimeout(timeInSeconds, SECONDS)
        .exceptionally(ex -> {
          log.warn("Nie powiodlo sie pobieranie nowych danych dla bloga {}", blog.getName(), ex);
          return false;
        });
  }

}
