package pl.michal.olszewski.rssaggregator.blog.rss.update;

import static pl.michal.olszewski.rssaggregator.item.LinkExtractor.getFinalURL;

import com.rometools.rome.feed.synd.SyndFeed;
import java.lang.Character.UnicodeBlock;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriUtils;
import pl.michal.olszewski.rssaggregator.blog.RssInfo;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

@Slf4j
class BlogItemsFromFeedExtractor {

  private BlogItemsFromFeedExtractor() {
  }

  static Set<ItemDTO> getItemsForBlog(SyndFeed syndFeed, RssInfo rssInfo) {
    log.trace("getItemsForBlog {} lastUpdatedDate {}", rssInfo.getBlogId(), rssInfo.getLastUpdateDate());
    return syndFeed.getEntries().parallelStream()
        .filter(entry -> entry.getPublishedDate().toInstant().isAfter(rssInfo.getLastUpdateDate()))
        .map(entry -> new ItemDTO(
            entry.getTitle(),
            Optional.ofNullable(entry.getDescription())
                .map(description -> HtmlTagRemover.removeHtmlTagFromDescription(entry.getDescription().getValue()))
                .orElse(""),
            getFinalURL(convertURLToAscii(entry.getLink())),
            entry.getPublishedDate().toInstant(),
            entry.getAuthor(),
            rssInfo.getBlogId()))
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
