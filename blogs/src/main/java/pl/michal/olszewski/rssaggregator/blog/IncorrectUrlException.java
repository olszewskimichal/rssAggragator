package pl.michal.olszewski.rssaggregator.blog;

class IncorrectUrlException extends RuntimeException {

  IncorrectUrlException(String message) {
    super(message);
  }
}
