package pl.michal.olszewski.rssaggregator.blog;

public class BlogAggregationDTOBuilder {

  private String blogId;
  private Long blogItemsCount;

  public BlogAggregationDTOBuilder blogId(String blogId) {
    this.blogId = blogId;
    return this;
  }

  public BlogAggregationDTOBuilder blogItemsCount(Long blogItemsCount) {
    this.blogItemsCount = blogItemsCount;
    return this;
  }

  public BlogAggregationDTO build() {
    return new BlogAggregationDTO(blogId, blogItemsCount);
  }
}