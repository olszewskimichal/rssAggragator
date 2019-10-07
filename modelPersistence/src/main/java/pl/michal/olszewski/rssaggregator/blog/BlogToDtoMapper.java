package pl.michal.olszewski.rssaggregator.blog;

class BlogToDtoMapper {

  static BlogDTO mapToBlogDto(Blog blog) {
    return new BlogDTOBuilder().id(blog.getId()).link(blog.getBlogURL()).description(blog.getDescription()).name(blog.getName()).feedURL(blog.getFeedURL()).publishedDate(blog.getPublishedDate())
        .build();
  }
}
