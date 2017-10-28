package pl.michal.olszewski.rssaggregator.service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.exception.RssException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RssExtractorService {

    private static List<ItemDTO> getItemsForBlog(SyndFeed syndFeed) {
        return syndFeed.getEntries().stream().map(entry -> new ItemDTO(entry.getTitle(), entry.getDescription() != null ? entry.getDescription().getValue() : "", entry.getLink(), entry.getPublishedDate().toInstant(), entry.getAuthor())).collect(Collectors.toList());
    }

    private BlogDTO getBlogInfo(SyndFeed syndFeed, String feedURL) {
        return new BlogDTO(syndFeed.getLink(), syndFeed.getDescription(), syndFeed.getTitle(), feedURL, syndFeed.getAuthor(), syndFeed.getPublishedDate().toInstant());
    }

    public BlogDTO getBlog(XmlReader xmlReader, String feedURL) {
        try (XmlReader reader = xmlReader) {
            SyndFeed feed = new SyndFeedInput().build(reader);
            BlogDTO blogInfo = getBlogInfo(feed, feedURL);
            blogInfo.setItemsList(getItemsForBlog(feed));
            return blogInfo;
        } catch (IOException | FeedException e) {
            throw new RssException(feedURL);
        }
    }
}
