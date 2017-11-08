package pl.michal.olszewski.rssaggregator.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.exception.RssException;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;
import pl.michal.olszewski.rssaggregator.service.UpdateBlogSchedule;

@Transactional
public class UpdateScheduleTest extends IntegrationTest {

  @Autowired
  private UpdateBlogSchedule blogSchedule;
  @Autowired
  private BlogRepository blogRepository;

  @Test
  public void contextLoads() {
  }

  @Test
  public void shouldUpdateBlog() {
    blogRepository.deleteAll();
    Blog blog = new Blog("https://devstyle.pl", "devstyle.pl", "devstyle.pl", "https://devstyle.pl/feed", null);
    blogRepository.save(blog);
    blogSchedule.updatesBlogs();
    Optional<Blog> updatedBlog = blogRepository.findById(blog.getId());
    assertThat(updatedBlog).isPresent();
    assertThat(updatedBlog.get().getItems()).isNotEmpty().hasSize(15);
  }

  @Test
  public void shouldNotUpdateBlog() {
    Blog blog = new Blog("https://devstyle.xxx", "DEVSTYLE", "devstyle", "https://devstyle.xxx/feed", null);
    blogRepository.save(blog);
    assertThatThrownBy(() -> blogSchedule.updatesBlogs()).isInstanceOf(RssException.class);
    blog = new Blog("https://devstyle.pl", "DEVSTYLE", "devstyle", "https://devstyle.pl/feed", null);
    blogRepository.save(blog);
  }


}
