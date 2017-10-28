package pl.michal.olszewski.rssaggregator.units.hashCodeEquals;

import pl.michal.olszewski.rssaggregator.entity.Blog;

public class BlogTest extends LocalEqualsHashCodeTest {
    @Override
    protected Object createInstance() {
        return new Blog("test", "", "", "", null);
    }

    @Override
    protected Object createNotEqualInstance() {
        return new Blog("test2", "", "", "", null);
    }
}
