package pl.michal.olszewski.rssaggregator.ogtags;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class InMemoryPageInfoExtractor implements PageInfoExtractor {

  @Override
  public Document getPageInfoFromUrl(String url) {
    Document document = new Document("url");
    Element titleMeta = document.appendElement("meta");
    titleMeta.attr("property", "og:title");
    titleMeta.attr("content", "title");

    Element descriptionMeta = document.appendElement("meta");
    descriptionMeta.attr("property", "og:description");
    descriptionMeta.attr("content", "description");

    Element imageMeta = document.appendElement("meta");
    imageMeta.attr("property", "og:image");
    imageMeta.attr("content", "imageUrl");
    return document;
  }
}
