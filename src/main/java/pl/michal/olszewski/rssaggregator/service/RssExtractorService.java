package pl.michal.olszewski.rssaggregator.service;

import com.rometools.rome.feed.synd.SyndFeed;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RssExtractorService {

    public  static List<ItemDTO> getItemsForBlog(SyndFeed syndFeed) {
        return syndFeed.getEntries().stream().map(entry -> new ItemDTO(entry.getTitle(), entry.getDescription() != null ? entry.getDescription().getValue() : "", entry.getLink(), entry.getPublishedDate().toInstant(), entry.getAuthor())).collect(Collectors.toList());
    }
}
