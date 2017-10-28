package pl.michal.olszewski.rssaggregator.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

import javax.annotation.PostConstruct;

@Service
@Slf4j
@Profile("development")
public class InitDb {
    private final BlogRepository blogRepository;

    public InitDb(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @PostConstruct
    public void populateDatabase() {
        Blog blog = new Blog("https://devstyle.pl", "DEVSTYLE", "devstyle", "https://devstyle.pl/feed", null);
        blogRepository.save(blog);
        blog = new Blog("http://http://jvm-bloggers.com", "JVM_BLOGGERS", "JVM_BLOGGERS", "http://jvm-bloggers.com/pl/rss", null);
        blogRepository.save(blog);
        log.debug(blog.toString());
    }
}
