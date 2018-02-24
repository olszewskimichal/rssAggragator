package pl.michal.olszewski.rssaggregator.service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.exception.RssException;

@Service
@Slf4j
public class RssExtractorService {

  private static Set<ItemDTO> getItemsForBlog(SyndFeed syndFeed, Instant lastUpdatedDate) {
    return syndFeed.getEntries().parallelStream()
        .filter(v -> v.getPublishedDate().toInstant().isAfter(lastUpdatedDate))
        .map(entry -> new ItemDTO(
            entry.getTitle(),
            entry.getDescription() != null ? entry.getDescription().getValue() : "",
            getFinalURL(convertURLToAscii(entry.getLink())),
            entry.getPublishedDate().toInstant(),
            entry.getAuthor()))
        .collect(Collectors.toSet());
  }

  public static String convertURLToAscii(String linkUrl) {
    try {
      String url = new URL(linkUrl.replaceAll(">", "%3E")).toURI().toString();
      if (containsUnicode(url)) {
        String asciiString = UriUtils.encodeQuery(url, "UTF-8");
        log.trace("Zamieniłem {} na {}", linkUrl, asciiString);
        return asciiString;
      }
    } catch (MalformedURLException | URISyntaxException | UnsupportedEncodingException e) {
      log.error("Wystapił problem przy zamianie linku na ASCII {}", e);
    }
    return linkUrl;
  }

  private static boolean containsUnicode(String url) {
    OptionalInt any = url.chars().parallel().filter(c -> UnicodeBlock.of(c) != UnicodeBlock.BASIC_LATIN).findAny();
    return any.isPresent();
  }

  public static String getFinalURL(String linkUrl) {
    try {
      HttpURLConnection con = (HttpURLConnection) new URL(linkUrl).openConnection();
      con.addRequestProperty("User-Agent", "Mozilla/4.76");
      con.setInstanceFollowRedirects(false);
      con.setRequestMethod("HEAD");
      con.setConnectTimeout(1000);
      con.connect();
      if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
        String redirectUrl = con.getHeaderField("Location");
        return getFinalURL(redirectUrl).replaceAll("[&?]gi.*", "");
      }
    } catch (IOException ignored) {
      log.error("Wystapil blad przy próbie wyciagniecia finalnego linku z {} o tresci ", linkUrl, ignored);
    }
    return linkUrl;
  }

  private BlogDTO getBlogInfo(SyndFeed syndFeed, String feedURL, String blogURL) {
    return new BlogDTO(syndFeed.getLink() != null ? syndFeed.getLink() : blogURL, syndFeed.getDescription(), syndFeed.getTitle(), feedURL, syndFeed.getPublishedDate().toInstant(), new ArrayList<>());
  }

  public BlogDTO getBlog(XmlReader xmlReader, String feedURL, String blogURL, Instant lastUpdatedDate) {
    log.debug("getBlogFromXml {} {}", feedURL, Thread.currentThread().getName());
    try (XmlReader reader = xmlReader) {
      SyndFeed feed = new SyndFeedInput().build(reader);
      feed.setEncoding("UTF-8");
      feed.getEntries().parallelStream().filter(v -> v.getPublishedDate() == null && v.getUpdatedDate() != null).forEach(v -> v.setPublishedDate(v.getUpdatedDate()));
      BlogDTO blogInfo = getBlogInfo(feed, feedURL, blogURL);
      getItemsForBlog(feed, lastUpdatedDate).forEach(blogInfo::addNewItem);
      return blogInfo;
    } catch (IOException | FeedException e) {
      throw new RssException(feedURL);
    }
  }
}
