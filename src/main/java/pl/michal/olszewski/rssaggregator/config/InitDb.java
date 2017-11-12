package pl.michal.olszewski.rssaggregator.config;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

@Service
@Slf4j
@Profile({"development", "prod"})
public class InitDb {

  private final BlogRepository blogRepository;

  public InitDb(BlogRepository blogRepository) {
    this.blogRepository = blogRepository;
  }

  //@PostConstruct
  public void populateDatabase() {
    Stream.of("https://devstyle.pl", "https://vladmihalcea.com", "http://jakoszczedzacpieniadze.pl", "https://kobietydokodu.pl", "https://codecouple.pl", "https://geekyprimitives.wordpress.com/",
        "https://aboullaite.me/", "http://www.javadevmatt.pl/")
        .map(v -> new Blog(v, "", "", v + "/feed", null))
        .forEach(blogRepository::save);
    blogRepository.save(new Blog("http://jvm-bloggers.com", "", "JVM-Bloggers", "http://jvm-bloggers.com/pl/rss", null));
    blogRepository.save(new Blog("https://programistanaswoim.pl", "", "ProgramistaNaSwoim", "http://feeds.feedburner.com/ProgramistaNaSwoim", null));
    blogRepository.save(new Blog("http://blog.codeleak.pl/", "", "Codeleak.pl", "http://blog.codeleak.pl/feeds/posts/default?alt=rss", null));
    blogRepository.save(new Blog("https://spring.io/blog", "", "Spring", "https://spring.io/blog.atom/", null));
  }

  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }
}
