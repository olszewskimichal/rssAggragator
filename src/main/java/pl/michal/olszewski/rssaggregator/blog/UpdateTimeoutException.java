package pl.michal.olszewski.rssaggregator.blog;

class UpdateTimeoutException extends RuntimeException {

  UpdateTimeoutException(String name) {
    super(String.format("Timeout aktualizacji bloga = %s", name));
  }

}
