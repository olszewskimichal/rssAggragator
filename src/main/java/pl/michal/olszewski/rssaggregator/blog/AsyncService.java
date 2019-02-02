package pl.michal.olszewski.rssaggregator.blog;

import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    Boolean updateBlog(Blog blog) {
        log.debug("START updateBlog dla blog {}", blog.getName());
        try {
            BlogDTO blogDTO = rssExtractorService.getBlog(new XmlReader(new URL(blog.getFeedURL())), blog.getRssInfo());
            blogService.updateBlog(blog, blogDTO)
                .doOnSuccess(v-> log.debug("STOP updateBlog dla blog {}", v.getName()))
                .block();
            return true;
        } catch (IOException e) {
            log.error("wystapił bład przy aktualizacji bloga o id {}", blog.getId(), e);
            throw new RssException(blog.getFeedURL(), e);
        }
    }

}
