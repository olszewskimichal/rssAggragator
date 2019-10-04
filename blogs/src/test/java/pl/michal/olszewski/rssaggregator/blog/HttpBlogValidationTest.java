package pl.michal.olszewski.rssaggregator.blog;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HttpBlogValidationTest {

  BlogValidation blogValidation = new HttpBlogValidation();

  @Test
  void shouldThrowExceptionWhenLinkUrlIsNull() {
    CreateBlogDTO createBlogDTO = CreateBlogDTO.builder().feedURL("feedUrl").build();

    assertThrows(IncompleteBlogException.class, () -> blogValidation.validate(createBlogDTO));
  }

  @Test
  void shouldThrowExceptionWhenFeedUrlIsNull() {
    CreateBlogDTO createBlogDTO = CreateBlogDTO.builder().link("linkUrl").build();

    assertThrows(IncompleteBlogException.class, () -> blogValidation.validate(createBlogDTO));
  }

  @Test
  void shouldThrowExceptionWhenLinkUrlIsIncorrect() {
    CreateBlogDTO createBlogDTO = CreateBlogDTO.builder().feedURL("http://54.38.53.100").link("linkUrl").build();

    assertThrows(IncorrectUrlException.class, () -> blogValidation.validate(createBlogDTO));
  }

  @Test
  void shouldThrowExceptionWhenFeedUrlIsIncorrect() {
    CreateBlogDTO createBlogDTO = CreateBlogDTO.builder().link("http://54.38.53.100").feedURL("feedUrl").build();

    assertThrows(IncorrectUrlException.class, () -> blogValidation.validate(createBlogDTO));
  }
}