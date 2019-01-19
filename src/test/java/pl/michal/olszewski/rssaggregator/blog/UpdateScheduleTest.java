package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.extenstions.TimeExecutionLogger;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;


class UpdateScheduleTest extends IntegrationTestBase implements TimeExecutionLogger {

  @Autowired
  private AsyncService asyncService;
  @Autowired
  private BlogRepository blogRepository;

  @Autowired
  private BlogService blogService;

  @BeforeEach
  void setUp() {
    blogService.evictBlogCache();
  }

  @Test
  void shouldUpdateBlog() throws ExecutionException, InterruptedException {
    blogRepository.deleteAll();
    Blog blog = new Blog("https://devstyle.pl", "devstyle.pl", "devstyle.pl", "https://devstyle.pl/feed", null, null);
    blogRepository.save(blog);
    Future<Void> voidFuture = asyncService.updateBlog(blog);
    voidFuture.get();
    Optional<Blog> updatedBlog = blogRepository.findById(blog.getId());
    assertAll(
        () -> assertThat(updatedBlog).isPresent(),
        () -> assertThat(updatedBlog.get().getItems()).isNotEmpty().hasSize(15),
        () -> assertThat(updatedBlog.get().getLastUpdateDate()).isNotNull().isBefore(Instant.now())
    );
  }

  @Test
  void shouldNotUpdateBlogWhenLastUpdatedDateIsAfterPublishedItems() throws ExecutionException, InterruptedException {
    blogRepository.deleteAll();
    Blog blog = blogRepository.save(new Blog("https://devstyle.pl", "devstyle.pl", "devstyle.pl", "https://devstyle.pl/feed", null, Instant.now()));
    Future<Void> voidFuture = asyncService.updateBlog(blog);
    voidFuture.get();
    Optional<Blog> updatedBlog = blogRepository.findById(blog.getId());

    assertAll(
        () -> assertThat(updatedBlog).isPresent(),
        () -> assertThat(updatedBlog.get().getItems()).isEmpty(),
        () -> assertThat(updatedBlog.get().getLastUpdateDate()).isNotNull().isBefore(Instant.now())
    );
  }

  @Test
  void shouldNotUpdateBlog() {
    Blog blog = new Blog("https://devstyle.xxx", "DEVSTYLE", "devstyle", "https://devstyle.xxx/feed", null, null);
    blogRepository.save(blog);
    assertThatThrownBy(() -> asyncService.updateBlog(blog).get())
        .isInstanceOf(ExecutionException.class)
        .hasCauseInstanceOf(RssException.class);
  }
}
