package pl.michal.olszewski.rssaggregator.blog.ogtags;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!integration")
public class JsoupPageInfoExtractor implements PageInfoExtractor {

  public Document getPageInfoFromUrl(String url) {
    try {
      return Jsoup.connect(url).userAgent("myUserAgent").get();
    } catch (IOException e) {
      log.warn("Nie mogę pobrać OG:Tagów z bloga {}", url);
      return null;
    }
  }
}