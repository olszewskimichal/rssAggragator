package pl.michal.olszewski.rssaggregator.units;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;

public class EqualsTest {

    @Test
    public void blogEqualsContractTest() {
        Item item1 = new Item(ItemDTO.builder().title("title").build());
        Item item2 = new Item(ItemDTO.builder().title("title2").build());
        EqualsVerifier.forClass(Blog.class).withPrefabValues(Item.class, item1, item2).withNonnullFields("items").verify();
    }

    @Test
    public void itemEqualsContractTest() {
        Blog blog1 = new Blog("test", "", "", "", null);
        Blog blog2 = new Blog("test2", "", "", "", null);
        EqualsVerifier.forClass(Item.class).withPrefabValues(Blog.class, blog1, blog2).withIgnoredFields("id", "blog").verify();
    }
}
