package pl.michal.olszewski.rssaggregator.blog.rss.update;

class UpdateTimeoutException extends RuntimeException {

  UpdateTimeoutException(String name) {
    super(String.format("Timeout aktualizacji bloga = %s", name));
  }

}
