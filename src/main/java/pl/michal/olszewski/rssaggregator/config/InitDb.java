package pl.michal.olszewski.rssaggregator.config;

import java.math.BigDecimal;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.ValueList;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;
import pl.michal.olszewski.rssaggregator.repository.ValueListRepository;

@Service
@Slf4j
@Profile("development")
public class InitDb {

  private final BlogRepository blogRepository;
  private final ValueListRepository valueListRepository;

  public InitDb(BlogRepository blogRepository, ValueListRepository valueListRepository) {
    this.blogRepository = blogRepository;
    this.valueListRepository = valueListRepository;
  }

  @PostConstruct
  public void populateDatabase() {
    /*Blog blog = new Blog("https://devstyle.pl", "DEVSTYLE", "devstyle", "https://devstyle.pl/feed", null);
    blogRepository.save(blog);
    blog = new Blog("http://http://jvm-bloggers.com", "JVM_BLOGGERS", "JVM_BLOGGERS", "http://jvm-bloggers.com/pl/rss", null);
    blogRepository.save(blog);
    log.debug(blog.toString());*/

    /*IntStream.rangeClosed(1, 5000000).forEach(v -> {
      if (v % 1000 == 0) {
        valueListRepository.flush();
      }
      valueListRepository
          .save(new ValueList(new BigDecimal(Math.random()), new BigDecimal(Math.random()), new BigDecimal(Math.random()), new BigDecimal(Math.random()), new BigDecimal(Math.random())));
    });*/
  }
}
