package pl.michal.olszewski.rssaggregator.assertion;

import java.util.List;
import org.assertj.core.api.ListAssert;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;

public class ItemListAssert extends ListAssert<ItemDTO> {

  private List<ItemDTO> actual;

  protected ItemListAssert(List<ItemDTO> blogList) {
    super(blogList);
    this.actual = blogList;
  }

  public static ItemListAssert assertThat(List<ItemDTO> actual) {
    return new ItemListAssert(actual);
  }

  public ItemListAssert isSuccessful() {
    assertThat(actual).isNotNull();
    return this;
  }

  public ItemListAssert hasNumberOfItems(int number) {
    assertThat(actual).hasSize(number);
    return this;
  }

}
