package pl.michal.olszewski.rssaggregator.blog;

class RssException extends RuntimeException {

  RssException(String feedURL) {
    super("Wystąpił błąd przy pobieraniu informacji z bloga " + feedURL);
  }
}
