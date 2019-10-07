package pl.michal.olszewski.rssaggregator.item;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class ItemEqualsTest {

  @Test
  void itemEqualsContractTest() {
    EqualsVerifier.forClass(Item.class)
        .withIgnoredFields("id", "createdAt", "read")
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void blogItemDTOEqualsContractTest() {
    EqualsVerifier.forClass(ReadItemDTO.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void blogItemLinkEqualsContractTest() {
    EqualsVerifier.forClass(BlogItemLink.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void blogItemDtoEqualsContractTest() {
    EqualsVerifier.forClass(BlogItemDTO.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void pageBlogItemDtoEqualsContractTest() {
    EqualsVerifier.forClass(PageBlogItemDTO.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void itemDtoEqualsContractTest() {
    EqualsVerifier.forClass(ItemDTO.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

  @Test
  void pageItemDtoEqualsContractTest() {
    EqualsVerifier.forClass(PageItemDTO.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }
}
