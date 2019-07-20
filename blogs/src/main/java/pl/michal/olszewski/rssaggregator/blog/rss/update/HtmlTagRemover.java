package pl.michal.olszewski.rssaggregator.blog.rss.update;

import org.jsoup.Jsoup;

class HtmlTagRemover {

  static String removeHtmlTagFromDescription(String description) {
    return Jsoup.parse(description).text();
  }

}
