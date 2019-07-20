package pl.michal.olszewski.rssaggregator.blog.rss.update;

import java.util.Optional;
import org.jsoup.Jsoup;

class HtmlTagRemover {

  static String removeHtmlTagFromDescription(String descriptionToEscape) {
    return Optional.ofNullable(descriptionToEscape)
        .map(description -> Jsoup.parse(description).text())
        .orElse(descriptionToEscape);
  }

}
