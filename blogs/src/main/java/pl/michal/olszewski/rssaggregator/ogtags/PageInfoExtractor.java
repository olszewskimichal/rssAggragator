package pl.michal.olszewski.rssaggregator.ogtags;

import org.jsoup.nodes.Document;

interface PageInfoExtractor {

  Document getPageInfoFromUrl(String url);
}
