package pl.michal.olszewski.rssaggregator.blog;

import static io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@Transactional
class UpdateBlogService {

  private final BlogReactiveRepository repository;
  private final AsyncService asyncService;
  private final Executor executor;

  public UpdateBlogService(BlogReactiveRepository repository, AsyncService asyncService, Executor executor, MeterRegistry registry) {
    this.repository = repository;
    this.asyncService = asyncService;
    if (registry != null) {
      this.executor = monitor(registry, executor, "prod_pool");
    } else {
      this.executor = executor;
    }
  }

  Mono<List<List<Boolean>>> updateAllActiveBlogsByRss() {
    log.debug("zaczynam aktualizacje blogów correlationId ");
    return Flux.merge(getUpdateBlogByRssList())
        .collectList()
        .doOnError(ex -> log.error("Aktualizacja zakonczona bledem ", ex));
  }

  private Mono<List<Boolean>> getUpdateBlogByRssList() {
    return repository.findAll()
        .flatMap(this::updateRssBlogItems)
        .collectList();
  }

  private Mono<Boolean> updateRssBlogItems(Blog blog) {
    String correlationId = UUID.randomUUID().toString();
    log.debug("Pobieranie nowych danych dla bloga {} correlationId {}", blog.getName(), correlationId);
    return Mono.fromCallable(() -> asyncService.updateRssBlogItems(blog, correlationId))
        .timeout(Duration.ofSeconds(4), Mono.error(new UpdateTimeoutException(blog.getName(), correlationId)))
        .doOnError(ex -> log.warn("Niepowiodlo sie pobieranie nowych danych dla bloga {} correlation Id {}", blog.getName(), correlationId, ex))
        .subscribeOn(Schedulers.fromExecutor(executor))
        .onErrorReturn(false);
  }

  public void refreshBlogFromId(String id, String correlationId) {
    log.debug("Odswiezam bloga o id {} correlationId {}", id, correlationId);
    repository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id, correlationId)))
        .map(blog -> asyncService.updateRssBlogItems(blog, correlationId))
        .subscribe();
  }

}
