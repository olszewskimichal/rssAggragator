package pl.michal.olszewski.rssaggregator.blog;

class RssException extends RuntimeException {

  RssException(String message, Throwable cause) {
    super("Wystąpił błąd przy pobieraniu informacji z bloga " + message, cause);
  }
}
