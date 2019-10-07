package pl.michal.olszewski.rssaggregator.blog;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HttpBlogValidationTest {

  BlogValidation blogValidation = new HttpBlogValidation();

  @Test
  void shouldThrowExceptionWhenLinkUrlIsNull() {
    CreateBlogDTO createBlogDTO = new CreateBlogDTOBuilder().feedURL("feedUrl").build();

    assertThrows(IncompleteBlogException.class, () -> blogValidation.validate(createBlogDTO.getLink(), createBlogDTO.getFeedURL()));
  }

  @Test
  void shouldThrowExceptionWhenFeedUrlIsNull() {
    CreateBlogDTO createBlogDTO = new CreateBlogDTOBuilder().link("linkUrl").build();

    assertThrows(IncompleteBlogException.class, () -> blogValidation.validate(createBlogDTO.getLink(), createBlogDTO.getFeedURL()));
  }

  @Test
  void shouldThrowExceptionWhenLinkUrlIsIncorrect() {
    CreateBlogDTO createBlogDTO = new CreateBlogDTOBuilder().feedURL("http://54.38.53.100").link("linkUrl").build();

    assertThrows(IncorrectUrlException.class, () -> blogValidation.validate(createBlogDTO.getLink(), createBlogDTO.getFeedURL()));
  }

  @Test
  void shouldThrowExceptionWhenFeedUrlIsIncorrect() {
    CreateBlogDTO createBlogDTO = new CreateBlogDTOBuilder().link("http://54.38.53.100").feedURL("feedUrl").build();

    assertThrows(IncorrectUrlException.class, () -> blogValidation.validate(createBlogDTO.getLink(), createBlogDTO.getFeedURL()));
  }
}