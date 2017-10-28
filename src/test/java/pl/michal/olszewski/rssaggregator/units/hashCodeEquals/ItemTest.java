package pl.michal.olszewski.rssaggregator.units.hashCodeEquals;

import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Item;

public class ItemTest extends LocalEqualsHashCodeTest {
    @Override
    protected Object createInstance() {
        return new Item(ItemDTO.builder().title("title").build());
    }

    @Override
    protected Object createNotEqualInstance() {
        return new Item(ItemDTO.builder().title("title2").build());
    }
}
