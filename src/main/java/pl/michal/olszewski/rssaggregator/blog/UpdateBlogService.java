package pl.michal.olszewski.rssaggregator.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
class UpdateBlogService {

    private final BlogRepository repository;
    private final AsyncService asyncService;

    @Value("${refresh.blog.enable-job}")
    private boolean enableJob;


    public UpdateBlogService(BlogRepository repository, AsyncService asyncService) {
        this.repository = repository;
        this.asyncService = asyncService;
    }

    @Scheduled(fixedDelayString = "${refresh.blog.milis}")
    public void updatesBlogs() {
        if (enableJob) {
            log.debug("zaczynam aktualizacje blogów");
            repository.findAll()
                .flatMap(v -> Mono.fromFuture(asyncService.updateBlog(v)))
                .collect(Collectors.toList()).block();
            log.debug("Aktualizacja zakończona");
        }
    }

    public void refreshBlogFromId(String id) {
        log.debug("Odswiezam bloga o id {}", id);
        Blog blog = repository.findById(id)
            .switchIfEmpty(Mono.error(new BlogNotFoundException(id)))
            .block();
        asyncService.updateBlog(blog);
    }

}
