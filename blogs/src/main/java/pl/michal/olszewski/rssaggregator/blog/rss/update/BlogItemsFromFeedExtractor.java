package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static pl.michal.olszewski.rssaggregator.item.LinkExtractor.getFinalURL;

import com.rometools.rome.feed.synd.SyndFeed;
import java.lang.Character.UnicodeBlock;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;
import pl.michal.olszewski.rssaggregator.blog.RssInfo;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.ItemDTOBuilder;

class BlogItemsFromFeedExtractor {

  private static final Logger log = LoggerFactory.getLogger(BlogItemsFromFeedExtractor.class);

  private BlogItemsFromFeedExtractor() {
  }

  static Set<ItemDTO> getItemsForBlog(SyndFeed syndFeed, RssInfo rssInfo) {
    log.trace("getItemsForBlog {} lastUpdatedDate {}", rssInfo.getBlogId(), rssInfo.getLastUpdateDate());
    return syndFeed.getEntries().parallelStream()
        .filter(entry -> entry.getPublishedDate().toInstant().isAfter(rssInfo.getLastUpdateDate()))
        .map(entry -> new ItemDTOBuilder().title(entry.getTitle()).description(entry.getDescription() != null ? HtmlTagRemover.removeHtmlTagFromDescription(entry.getDescription().getValue()) : "")
            .link(getFinalURL(convertURLToAscii(entry.getLink()))).date(entry.getPublishedDate().toInstant()).author(entry.getAuthor()).blogId(rssInfo.getBlogId()).build())
        .collect(Collectors.toSet());
  }

  static String convertURLToAscii(String linkUrl) {
    try {
      var url = new URL(linkUrl.replaceAll(">", "%3E")).toURI().toString();
      if (containsUnicode(url)) {
        String asciiString = UriUtils.encodeQuery(url, "UTF-8");
        log.trace("Zamieniłem {} na {}", linkUrl, asciiString);
        return asciiString;
      }
    } catch (MalformedURLException | URISyntaxException e) {
      log.error("Wystapił problem przy zamianie linku {} na ASCII", linkUrl, e);
    }
    return linkUrl;
  }

  private static boolean containsUnicode(String url) {
    OptionalInt any = url.chars().parallel().filter(c -> UnicodeBlock.of(c) != UnicodeBlock.BASIC_LATIN).findAny();
    return any.isPresent();
  }

}
