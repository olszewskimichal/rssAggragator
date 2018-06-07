package pl.michal.olszewski.rssaggregator.item;

import java.util.List;
import org.assertj.core.api.ListAssert;

public class ItemListAssert extends ListAssert<ItemDTO> {

  private final List<ItemDTO> actual;

  private ItemListAssert(List<ItemDTO> blogList) {
    super(blogList);
    this.actual = blogList;
  }

  public static ItemListAssert assertThat(List<ItemDTO> actual) {
    return new ItemListAssert(actual);
  }

  ItemListAssert isSuccessful() {
    assertThat(actual).isNotNull();
    return this;
  }

  ItemListAssert hasNumberOfItems(int number) {
    assertThat(actual).hasSize(number);
    return this;
  }

}
