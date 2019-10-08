package pl.michal.olszewski.rssaggregator.item;

import io.sentry.Sentry;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkExtractor {
  private static final Logger log = LoggerFactory.getLogger(LinkExtractor.class);

  public static String getFinalURL(String linkUrl) {
    try {
      log.trace("getFinalURL for link {}", linkUrl);
      var con = (HttpURLConnection) new URL(linkUrl).openConnection();
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
    try {
      return getUrlWithoutParameters(linkUrl);
    } catch (URISyntaxException ex) {
      Sentry.capture(ex);
      log.error("Wystapil blad przy próbie usuwania parametrów z linku {} o tresci ", linkUrl, ex);
      return linkUrl;
    }
  }

  static private String getUrlWithoutParameters(String url) throws URISyntaxException {
    var uri = new URI(url);
    return new URI(uri.getScheme(),
        uri.getAuthority(),
        uri.getPath(),
        null, // Ignore the query part of the input url
        uri.getFragment()).toString();
  }
}
