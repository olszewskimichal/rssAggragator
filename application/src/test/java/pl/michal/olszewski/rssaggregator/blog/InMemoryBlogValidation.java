package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Primary
@Profile("test")
public class InMemoryBlogValidation implements BlogValidation {

  @Override
  public void validate(String blogUrl, String feedUrl) {

  }
}
