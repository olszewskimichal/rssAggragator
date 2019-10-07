package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class IncompleteBlogException extends RuntimeException {

  IncompleteBlogException() {
    super("Uzupełnij podstawowe dane na temat blog tzn. url do bloga oraz url do feeda");
  }
}
