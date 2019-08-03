package pl.michal.olszewski.rssaggregator.blog;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class EqualsTest {

  @Test
  void blogEqualsContractTest() {
    EqualsVerifier.forClass(Blog.class)
        .withIgnoredFields("id")
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

}
