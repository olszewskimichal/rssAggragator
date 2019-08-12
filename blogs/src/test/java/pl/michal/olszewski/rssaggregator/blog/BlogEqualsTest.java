package pl.michal.olszewski.rssaggregator.blog;

import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class BlogEqualsTest {

  @Test
  void blogEqualsContractTest() {
    EqualsVerifier.forClass(Blog.class)
        .withIgnoredFields("id")
        .suppress(NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void blogAggregationDtoEqualsContractTest() {
    EqualsVerifier.forClass(BlogAggregationDTO.class)
        .suppress(NONFINAL_FIELDS)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  void blogDtoEqualsContractTest() {
    EqualsVerifier.forClass(BlogDTO.class)
        .suppress(NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void updateBlogDTOEqualsContractTest() {
    EqualsVerifier.forClass(UpdateBlogDTO.class)
        .suppress(NONFINAL_FIELDS)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  void updateBlogWithItemsDTOEqualsContractTest() {
    EqualsVerifier.forClass(UpdateBlogWithItemsDTO.class)
        .suppress(NONFINAL_FIELDS)
        .withRedefinedSuperclass()
        .verify();
  }

  @Test
  void createBlogDtoEqualsContractTest() {
    EqualsVerifier.forClass(CreateBlogDTO.class)
        .suppress(NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void eventsContractTest() {
    EqualsVerifier.forClass(ChangeActivityBlogEvent.class)
        .suppress(NONFINAL_FIELDS)
        .verify();
    EqualsVerifier.forClass(ActivateBlog.class)
        .suppress(NONFINAL_FIELDS)
        .verify();
    EqualsVerifier.forClass(DeactivateBlog.class)
        .suppress(NONFINAL_FIELDS)
        .verify();
  }
}
