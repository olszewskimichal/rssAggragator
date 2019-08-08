package pl.michal.olszewski.rssaggregator.item;

import java.util.Optional;
import org.jsoup.Jsoup;

class HtmlTagRemover {

  private HtmlTagRemover() {
  }

  static String removeHtmlTagFromDescription(String descriptionToEscape) {
    return Optional.ofNullable(descriptionToEscape)
        .map(description -> Jsoup.parse(description).text())
        .orElse(descriptionToEscape);
  }
}
