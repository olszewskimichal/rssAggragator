package pl.michal.olszewski.rssaggregator.blog;

import static io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
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

  Mono<List<List<Boolean>>> updateAllActiveBlogs() {
    log.debug("zaczynam aktualizacje blogów");
    return Flux.merge(getUpdateBlogList())
        .collectList()
        .doOnError(ex -> log.error("Aktualizacja zakonczona bledem ", ex));
  }

  private Mono<List<Boolean>> getUpdateBlogList() {
    return repository.findAll()
        .flatMap(this::updateBlog)
        .collectList();
  }

  private Mono<Boolean> updateBlog(Blog blog) {
    log.debug("update blog {}", blog.getName());
    return Mono.fromCallable(() -> asyncService.updateBlog(blog))
        .subscribeOn(Schedulers.fromExecutor(executor))
        .onErrorReturn(false);
  }

  public void refreshBlogFromId(String id, String correlationId) {
    log.debug("Odswiezam bloga o id {} correlationId {}", id, correlationId);
    repository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id, correlationId)))
        .map(asyncService::updateBlog)
        .subscribe();
  }

}
