package pl.michal.olszewski.rssaggregator.blog;

class BlogToDtoMapper {

  private BlogToDtoMapper() {
  }

  static BlogDTO mapToBlogDto(Blog blog) {
    return new BlogDTO(
        blog.getId(),
        blog.getBlogURL(),
        blog.getDescription(),
        blog.getName(),
        blog.getFeedURL(),
        blog.getPublishedDate(),
        blog.getImageUrl());
  }
}
