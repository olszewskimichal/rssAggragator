package pl.michal.olszewski.rssaggregator.search.items;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class ItemSearchEqualsTest {

  @Test
  void itemSearchResultEqualsContractTest() {
    EqualsVerifier.forClass(ItemSearchResult.class)
        .suppress(Warning.NONFINAL_FIELDS)
        .verify();
  }

}