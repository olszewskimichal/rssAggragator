package pl.michal.olszewski.rssaggregator.exception;

public class BlogNotFoundException extends RuntimeException {

  public BlogNotFoundException(String name) {
    super("Nie znaleziono blogu = " + name);
  }

  public BlogNotFoundException(Long id) {
    super("Nie znaleziono bloga o id = " + id);
  }
}
