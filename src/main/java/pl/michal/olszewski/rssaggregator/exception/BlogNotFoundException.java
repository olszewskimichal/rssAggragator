package pl.michal.olszewski.rssaggregator.exception;

public class BlogNotFoundException extends RuntimeException {
    public BlogNotFoundException(String url) {
        super("Nie znaleziono blogu o URL = " + url);
    }

    public BlogNotFoundException(Long id) {
        super("Nie znaleziono bloga o id = " + id);
    }
}
