package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class IncorrectUrlException extends RuntimeException {

  IncorrectUrlException(String message) {
    super(message);
  }
}
