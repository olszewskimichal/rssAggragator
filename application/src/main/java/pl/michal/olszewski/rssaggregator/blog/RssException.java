package pl.michal.olszewski.rssaggregator.blog;

class RssException extends RuntimeException {

  RssException(String feedUrl, Throwable cause) {
    super(String.format("Wystąpił błąd przy pobieraniu informacji z bloga %s", feedUrl), cause);
  }
}
