package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
class BlogNotFoundException extends RuntimeException {

  BlogNotFoundException(String name) {
    super("Nie znaleziono bloga = " + name);
  }

}
