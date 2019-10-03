package pl.michal.olszewski.rssaggregator.blog.ogtags;

import org.jsoup.nodes.Document;

public interface PageInfoExtractor {
  Document getPageInfoFromUrl(String url);
}
