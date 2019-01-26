package pl.michal.olszewski.rssaggregator.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.Executor;

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
    Disposable runScheduledUpdate() {
        return updateAllActiveBlogs().subscribe();
    }

    Mono<List<List<Boolean>>> updateAllActiveBlogs() {
        if (enableJob) {
            log.debug("zaczynam aktualizacje blogów");
            Mono<List<Boolean>> collect = repository.findAll()
                .flatMap(this::updateBlog)
                .collectList();
            return Flux.merge(collect)
                .subscribeOn(Schedulers.parallel())
                .collectList()
                .doOnSuccess(v -> log.debug("Aktualizacja zakonczona"));
        }
        return Mono.empty();
    }

    private Mono<Boolean> updateBlog(Blog blog) {
        log.error("update blog {}", blog);
        return Mono.fromCallable(() -> asyncService.updateBlog(blog))
            .subscribeOn(Schedulers.fromExecutor(executor))
            .doOnNext(v -> log.error("Przetworzylem {}", v))
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
