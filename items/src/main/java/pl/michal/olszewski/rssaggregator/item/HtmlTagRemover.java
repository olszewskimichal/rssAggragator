package pl.michal.olszewski.rssaggregator.item;

import org.jsoup.Jsoup;

class HtmlTagRemover {

  static String removeHtmlTagFromDescription(String description) {
    return Jsoup.parse(description).text();
  }

}
