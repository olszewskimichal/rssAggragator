package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics.monitor;

import brave.Tracer;
import com.rometools.fetcher.FeedFetcher;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogReactiveRepository;
import pl.michal.olszewski.rssaggregator.blog.BlogService;
import pl.michal.olszewski.rssaggregator.blog.failure.BlogUpdateFailedEvent;
import pl.michal.olszewski.rssaggregator.blog.failure.BlogUpdateFailedEventProducer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
class UpdateBlogService {

  private final BlogReactiveRepository repository;
  private final Executor executor;
  private final RssExtractorService rssExtractorService;
  private final BlogService blogService;
  private final BlogUpdateFailedEventProducer blogUpdateFailedEventProducer;
  private final Tracer tracer;

  public UpdateBlogService(
      BlogReactiveRepository repository,
      Executor executor,
      MeterRegistry registry,
      BlogService blogService,
      BlogUpdateFailedEventProducer blogUpdateFailedEventProducer,
      FeedFetcher feedFetcher,
      Tracer tracer
  ) {
    this.repository = repository;
    this.blogUpdateFailedEventProducer = blogUpdateFailedEventProducer;
    this.tracer = tracer;
    this.rssExtractorService = new RssExtractorService(feedFetcher, blogUpdateFailedEventProducer, this.tracer);
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

  private Mono<List<Boolean>> getUpdateBlogByRssList() {
    return repository.findAll()
        .flatMap(this::updateRssBlogItems)
        .collectList();
  }

  Mono<Boolean> updateRssBlogItems(Blog blog) {
    log.debug("Pobieranie nowych danych dla bloga {}", blog.getName());
    return Mono.fromCallable(() -> extractBlogFromRssAndUpdateBlog(blog))
        .timeout(Duration.ofSeconds(5), Mono.error(new UpdateTimeoutException(blog.getName())))
        .doOnError(ex -> {
              if (ex instanceof UpdateTimeoutException) {
                blogUpdateFailedEventProducer.writeEventToQueue(new BlogUpdateFailedEvent(Instant.now(), tracer.currentSpan().context().toString(), blog.getFeedURL(), blog.getId(), ex.getMessage()));
              }
          log.warn("Nie powiodlo sie pobieranie nowych danych dla bloga {} correlation Id {}", blog.getName(), ex);
            }
        )
        .subscribeOn(Schedulers.fromExecutor(executor))
        .onErrorReturn(false);
  }

  private Boolean extractBlogFromRssAndUpdateBlog(Blog blog) {
    log.trace("START updateRssBlogItems dla blog {}", blog.getName());
    var blogDTO = rssExtractorService.getBlog(blog.getRssInfo());
    blogService.updateBlog(blog, blogDTO)
        .doOnSuccess(updatedBlog -> log.trace("STOP updateRssBlogItems dla blog {}", updatedBlog.getName()))
        .block();
    return true; //TODO pozbyc się block?
  }


}
