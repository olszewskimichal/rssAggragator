package pl.michal.olszewski.rssaggregator.blog;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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

  @Value("${refresh.blog.enable-job}")
  private boolean enableJob;

  public UpdateBlogService(BlogReactiveRepository repository, AsyncService asyncService, Executor executor) {
    this.repository = repository;
    this.asyncService = asyncService;
    this.executor = executor;
  }

  @Scheduled(fixedDelayString = "${refresh.blog.milis}")
  void runScheduledUpdate() {
    updateAllActiveBlogs()
        .subscribe();
  }

  Mono<List<List<Boolean>>> updateAllActiveBlogs() {
    if (enableJob) {
      var now = Instant.now();
      log.debug("zaczynam aktualizacje blogów");
      return Flux.merge(getUpdateBlogList())
          .collectList()
          .doOnSuccess(v -> log.debug("Aktualizacja zakonczona w {} sekund", Duration.between(now, Instant.now()).getSeconds()))
          .doOnError(ex -> log.error("Aktualizacja zakonczona bledem ", ex));
    }
    return Mono.empty();
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

  public void refreshBlogFromId(String id) {
    log.debug("Odswiezam bloga o id {}", id);
    repository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
        .map(asyncService::updateBlog)
        .subscribe();
  }

}
