package pl.michal.olszewski.rssaggregator.blog.rss.update;

import com.rometools.rome.feed.synd.SyndFeed;
import io.sentry.Sentry;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriUtils;
import pl.michal.olszewski.rssaggregator.blog.Blog.RssInfo;
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
            entry.getDescription() != null ? HtmlTagRemover.removeHtmlTagFromDescription(entry.getDescription().getValue()) : "",
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

  static String getFinalURL(String linkUrl) {
    try {
      log.trace("getFinalURL for link {}", linkUrl);
      HttpURLConnection con = (HttpURLConnection) new URL(linkUrl).openConnection();
      con.addRequestProperty("User-Agent", "Mozilla/4.76");
      con.setInstanceFollowRedirects(false);
      con.setRequestMethod("HEAD");
      con.setConnectTimeout(700);
      con.connect();
      if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
        log.trace("wykonuje redirect dla linku {}", linkUrl);
        String redirectUrl = con.getHeaderField("Location");
        return getFinalURL(redirectUrl).replaceAll("[&?]gi.*", "");
      }
    } catch (IOException ex) {
      Sentry.capture(ex);
      log.error("Wystapil blad przy próbie wyciagniecia finalnego linku z {} o tresci ", linkUrl, ex);
    }
    return linkUrl;
  }

}
