package pl.michal.olszewski.rssaggregator.blog.ogtags;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPageInfoExtractor implements PageInfoExtractor {

  @Override
  public Document getPageInfoFromUrl(String url) {
    Document document = new Document("url");
    Element titleMeta = document.appendElement("meta");
    titleMeta.attr("property", "og:title");
    titleMeta.attr("content", "test");

    Element descriptionMeta = document.appendElement("meta");
    descriptionMeta.attr("property", "og:description");
    descriptionMeta.attr("content", "test2");

    Element imageMeta = document.appendElement("meta");
    imageMeta.attr("property", "og:image");
    imageMeta.attr("content", "test3");
    return document;
  }
}
