package pl.michal.olszewski.rssaggregator.blog.ogtags;

import org.jsoup.nodes.Document;

interface PageInfoExtractor {

  Document getPageInfoFromUrl(String url);
}
