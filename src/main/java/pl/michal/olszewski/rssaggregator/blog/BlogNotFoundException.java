package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
class BlogNotFoundException extends RuntimeException {

  private final String correlationID;

  BlogNotFoundException(String name, String correlationID) {
    super("Nie znaleziono bloga = " + name + " correlationID = " + correlationID);
    this.correlationID = correlationID;
  }

  @Override
  public String toString() {
    return super.toString() + " correlationID " + correlationID;
  }
}
