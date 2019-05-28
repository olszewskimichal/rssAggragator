package pl.michal.olszewski.rssaggregator.blog;

import static io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics.monitor;
import static net.logstash.logback.encoder.org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.events.failed.BlogUpdateFailedEvent;
import pl.michal.olszewski.rssaggregator.events.failed.BlogUpdateFailedEventProducer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@Transactional
class UpdateBlogService {

  private static final Scheduler SCHEDULER = Schedulers.fromExecutor(Executors.newFixedThreadPool(16));
  private final BlogReactiveRepository repository;
  private final Executor executor;
  private final RssExtractorService rssExtractorService;
  private final BlogService blogService;
  private final BlogUpdateFailedEventProducer blogUpdateFailedEventProducer;

  public UpdateBlogService(BlogReactiveRepository repository, Executor executor, MeterRegistry registry, BlogService blogService,
      BlogUpdateFailedEventProducer blogUpdateFailedEventProducer) {
    this.repository = repository;
    this.blogUpdateFailedEventProducer = blogUpdateFailedEventProducer;
    this.rssExtractorService = new RssExtractorService(blogUpdateFailedEventProducer);
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
        .flatMapIterable(v -> v)
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
    String correlationId = UUID.randomUUID().toString();
    log.debug("Pobieranie nowych danych dla bloga {} correlationId {}", blog.getName(), correlationId);
    return Mono.fromCallable(() -> updateRssBlogItems(blog, correlationId))
        .timeout(Duration.ofSeconds(5), Mono.error(new UpdateTimeoutException(blog.getName(), correlationId)))
        .doOnError(ex -> {
              if (ex instanceof UpdateTimeoutException) {
                blogUpdateFailedEventProducer.writeEventToQueue(new BlogUpdateFailedEvent(Instant.now(), correlationId, blog.getFeedURL(), blog.getId(), ex.getMessage(), getStackTrace(ex)));
              }
              log.warn("Niepowiodlo sie pobieranie nowych danych dla bloga {} correlation Id {}", blog.getName(), correlationId, ex);
            }
        )
        .subscribeOn(Schedulers.fromExecutor(executor))
        .onErrorReturn(false);
  }

  private Boolean updateRssBlogItems(Blog blog, String correlationID) {
    log.trace("START updateRssBlogItems dla blog {} correlationID {}", blog.getName(), correlationID);
    var blogDTO = rssExtractorService.getBlog(blog.getRssInfo(), correlationID);
    blogService.updateBlog(blog, blogDTO)
        .subscribeOn(SCHEDULER)
        .doOnSuccess(v -> log.trace("STOP updateRssBlogItems dla blog {} correlationID {}", v.getName(), correlationID))
        .block();
    return true;
  }


}
