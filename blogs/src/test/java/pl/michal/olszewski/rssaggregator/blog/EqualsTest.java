package pl.michal.olszewski.rssaggregator.blog;

import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class EqualsTest {

  @Test
  void blogEqualsContractTest() {
    EqualsVerifier.forClass(Blog.class)
        .withIgnoredFields("id")
        .suppress(NONFINAL_FIELDS)
        .verify();
  }

}
