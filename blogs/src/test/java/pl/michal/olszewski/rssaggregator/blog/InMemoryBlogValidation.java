package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
class InMemoryBlogValidation implements BlogValidation {

  @Override
  public void validate(CreateBlogDTO createBlogDTO) {

  }
}
