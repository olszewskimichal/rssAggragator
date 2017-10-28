package pl.michal.olszewski.rssaggregator.service;

import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.exception.RssException;

import java.io.IOException;
import java.net.URL;

@Service
@Slf4j
public class UpdateBlogSchedule {
    private final BlogService blogService;
    private final RssExtractorService rssExtractorService;


    public UpdateBlogSchedule(BlogService blogService) {
        this.blogService = blogService;
        this.rssExtractorService = new RssExtractorService();
    }

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    public void updatesBlogs() {
        log.debug("zaczynam aktualizacje blogów");
        blogService.getAllBlogs().forEach(this::updateBlog);
        log.debug("Aktualizacja zakończona");
    }

    private void updateBlog(Blog v) {
        try {
            BlogDTO blogDTO = rssExtractorService.getBlog(new XmlReader(new URL(v.getFeedURL())), v.getFeedURL(), v.getBlogURL());
            blogService.updateBlog(blogDTO);
        } catch (IOException e) {
            log.error("wystapił bład przy aktualizacji bloga o id {} o tresci {}", v.getId(), e);
            throw new RssException(v.getFeedURL());
        }
    }
}
