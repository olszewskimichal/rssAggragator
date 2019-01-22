package pl.michal.olszewski.rssaggregator.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.extenstions.TimeExecutionLogger;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


class UpdateScheduleTest extends IntegrationTestBase implements TimeExecutionLogger {

    @Autowired
    private AsyncService asyncService;
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogService blogService;

    @BeforeEach
    void setUp() {
        blogRepository.deleteAll().block();
        blogService.evictBlogCache();
    }

    @Test
    void shouldUpdateBlog() throws ExecutionException, InterruptedException {
        Blog blog = new Blog("https://devstyle.pl", "devstyle.pl", "devstyle.pl", "https://devstyle.pl/feed", null, null);
        blogRepository.save(blog).block();
        CompletableFuture<Blog> voidFuture = asyncService.updateBlog(blog);
        voidFuture.get();
        Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());
        assertAll(
            () -> assertThat(updatedBlog.block().getItems()).isNotEmpty().hasSize(15),
            () -> assertThat(updatedBlog.block().getLastUpdateDate()).isNotNull().isBefore(Instant.now())
        );
    }

    @Test
    void shouldNotUpdateBlogWhenLastUpdatedDateIsAfterPublishedItems() throws ExecutionException, InterruptedException {
        Blog blog = blogRepository.save(new Blog("https://devstyle.pl", "devstyle.pl", "devstyle.pl", "https://devstyle.pl/feed", null, Instant.now())).block();
        CompletableFuture<Blog> voidFuture = asyncService.updateBlog(blog);
        voidFuture.get();
        Mono<Blog> updatedBlog = blogRepository.findById(blog.getId());
        assertAll(
            () -> assertThat(updatedBlog.block().getItems()).isEmpty(),
            () -> assertThat(updatedBlog.block().getLastUpdateDate()).isNotNull().isBefore(Instant.now())
        );
    }

    @Test
    void shouldNotUpdateBlog() {
        Blog blog = new Blog("https://devstyle.xxx", "DEVSTYLE", "devstyle", "https://devstyle.xxx/feed", null, null);
        blogRepository.save(blog).block();
        assertThatThrownBy(() -> asyncService.updateBlog(blog).get())
            .isInstanceOf(ExecutionException.class)
            .hasCauseInstanceOf(RssException.class);
    }
}
