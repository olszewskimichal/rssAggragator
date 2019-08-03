package pl.michal.olszewski.rssaggregator.item;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class EqualsTest {

  @Test
  void itemEqualsContractTest() {
    EqualsVerifier.forClass(Item.class)
        .withIgnoredFields("id", "createdAt", "read")
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }
}
