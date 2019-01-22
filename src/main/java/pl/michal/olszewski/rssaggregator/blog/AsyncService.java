package pl.michal.olszewski.rssaggregator.blog;

import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@Slf4j
class AsyncService {

    private final BlogService blogService;
    private final RssExtractorService rssExtractorService;

    public AsyncService(BlogService blogService) {
        this.blogService = blogService;
        this.rssExtractorService = new RssExtractorService();
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Blog> updateBlog(Blog v) {
        log.debug("START updateBlog dla blog {}", v.getName());
        try {
            BlogDTO blogDTO = rssExtractorService.getBlog(new XmlReader(new URL(v.getFeedURL())), v.getFeedURL(), v.getBlogURL(), v.getLastUpdateDate() == null ? Instant.MIN : v.getLastUpdateDate());
            Blog block = blogService.updateBlog(blogDTO).block();
            log.debug("STOP updateBlog dla blog {}", v.getName());
            return CompletableFuture.completedFuture(block);
        } catch (IOException e) {
            log.error("wystapił bład przy aktualizacji bloga o id {}", v.getId(), e);
            throw new RssException(v.getFeedURL(), e);
        }
    }

}
