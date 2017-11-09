package pl.michal.olszewski.rssaggregator.exception;

public class RssException extends RuntimeException {

  public RssException(String feedURL) {
    super("Wystąpił błąd przy pobieraniu informacji z bloga " + feedURL);
  }
}
