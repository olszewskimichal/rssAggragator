package pl.michal.olszewski.rssaggregator.blog;

class BlogNotFoundException extends RuntimeException {

  BlogNotFoundException(String name) {
    super("Nie znaleziono blogu = " + name);
  }

  BlogNotFoundException(Long id) {
    super("Nie znaleziono bloga o id = " + id);
  }
}
