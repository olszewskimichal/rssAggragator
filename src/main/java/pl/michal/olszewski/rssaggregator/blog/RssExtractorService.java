package pl.michal.olszewski.rssaggregator.blog;

import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.FetcherException;
import com.rometools.fetcher.impl.HttpClientFeedFetcher;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import pl.michal.olszewski.rssaggregator.config.FeedFetcherCache;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;

@Service
@Slf4j
class RssExtractorService {

  private final FeedFetcherCache feedFetcherCache;

  RssExtractorService(FeedFetcherCache feedFetcherCache) {
    this.feedFetcherCache = feedFetcherCache;
  }

  private static Set<ItemDTO> getItemsForBlog(SyndFeed syndFeed, Instant lastUpdatedDate) {
    log.trace("getItemsForBlog lastUpdatedDate {}", lastUpdatedDate);
    return syndFeed.getEntries().parallelStream()
        .filter(entry -> entry.getPublishedDate().toInstant().isAfter(lastUpdatedDate))
        .map(entry -> new ItemDTO(
            entry.getTitle(),
            entry.getDescription() != null ? entry.getDescription().getValue() : "",
            getFinalURL(convertURLToAscii(entry.getLink())),
            entry.getPublishedDate().toInstant(),
            entry.getAuthor()))
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
    } catch (IOException ignored) {
      log.error("Wystapil blad przy próbie wyciagniecia finalnego linku z {} o tresci ", linkUrl, ignored);
    }
    return linkUrl;
  }

  private BlogDTO getBlogInfo(SyndFeed syndFeed, String feedURL, String blogURL) {
    log.trace("getBlogInfo feedURL {} blogURL {}", feedURL, blogURL);
    return new BlogDTO(
        syndFeed.getLink() != null ? syndFeed.getLink() : blogURL,
        syndFeed.getDescription(),
        syndFeed.getTitle(),
        feedURL,
        syndFeed.getPublishedDate() != null ? syndFeed.getPublishedDate().toInstant() : Instant.now(),
        new ArrayList<>());
  }

  BlogDTO getBlog(Blog.RssInfo info, String correlationID) {
    log.trace("getBlog START {} correlationID {}", info, correlationID);
    try {
      FeedFetcher feedFetcher = new HttpClientFeedFetcher(feedFetcherCache);
      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
      SSLContext.setDefault(ctx);
      URL url = new URL(info.getFeedURL());
      SyndFeed feed = feedFetcher.retrieveFeed("MyAgent", url);
      feed.setEncoding("UTF-8");
      feed.getEntries()
          .parallelStream()
          .filter(v -> v.getPublishedDate() == null && v.getUpdatedDate() != null)
          .forEach(v -> v.setPublishedDate(v.getUpdatedDate()));
      BlogDTO blogInfo = getBlogInfo(feed, info.getFeedURL(), info.getBlogURL());
      getItemsForBlog(feed, info.getLastUpdateDate())
          .forEach(blogInfo::addNewItem);
      log.trace("getBlog STOP {} correlationID {}", info, correlationID);
      return blogInfo;
    } catch (IOException | FeedException | FetcherException | NoSuchAlgorithmException | KeyManagementException e) {
      log.error("wystapił bład przy pobieraniu bloga {} correlationID {}", info, correlationID, e);
      throw new RssException(info.getFeedURL(), correlationID, e);
    }
  }

  static class DefaultTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }
  }
}
