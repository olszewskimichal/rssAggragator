package pl.michal.olszewski.rssaggregator.assertion;

import org.assertj.core.api.ListAssert;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;

import java.util.List;

public class BlogListAssert extends ListAssert<BlogDTO> {
    private List<BlogDTO> actual;

    protected BlogListAssert(List<BlogDTO> blogList) {
        super(blogList);
        this.actual = blogList;
    }

    public static BlogListAssert assertThat(List<BlogDTO> actual) {
        return new BlogListAssert(actual);
    }

    public BlogListAssert isSuccessful() {
        assertThat(actual).isNotNull();
        return this;
    }

    public BlogListAssert hasNumberOfItems(int number) {
        assertThat(actual).hasSize(number);
        return this;
    }

}
