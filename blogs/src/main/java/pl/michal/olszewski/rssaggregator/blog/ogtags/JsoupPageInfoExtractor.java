package pl.michal.olszewski.rssaggregator.blog.ogtags;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!integration")
class JsoupPageInfoExtractor implements PageInfoExtractor {

  private static final Logger log = LoggerFactory.getLogger(JsoupPageInfoExtractor.class);

  public Document getPageInfoFromUrl(String url) {
    try {
      return Jsoup.connect(url).userAgent("myUserAgent").get();
    } catch (IOException | IllegalArgumentException e) {
      log.warn("Nie mogę pobrać OG:Tagów z bloga {}", url, e);
      return null;
    }
  }
}