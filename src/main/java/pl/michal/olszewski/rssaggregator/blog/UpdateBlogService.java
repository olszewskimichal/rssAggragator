package pl.michal.olszewski.rssaggregator.blog;

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
  public void updatesBlogs() {
    if (enableJob) {
      log.debug("zaczynam aktualizacje blogów");
      Mono<List<Boolean>> collect = repository.findAll()
          .flatMap(v -> Mono.fromCallable(() -> asyncService.updateBlog(v)).subscribeOn(Schedulers.fromExecutor(executor)).onErrorReturn(false)) //TODO skrocic linie
          .collectList();
      Flux.merge(collect)
          .subscribeOn(Schedulers.parallel())
          .collectList().block(); //TODO usunac blocka
      log.debug("Aktualizacja zakończona");
    }
  }

  public void refreshBlogFromId(String id) {
    log.debug("Odswiezam bloga o id {}", id);
    Mono<Blog> blog = repository.findById(id)
        .switchIfEmpty(Mono.error(new BlogNotFoundException(id)));
    asyncService.updateBlog(blog.block()); //TODO usunac blocka
  }

}
