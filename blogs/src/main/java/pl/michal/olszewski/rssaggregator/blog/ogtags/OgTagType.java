package pl.michal.olszewski.rssaggregator.blog.ogtags;

import java.util.stream.Stream;

enum OgTagType {
  TITLE("og:title"),
  DESCRIPTION("og:description"),
  IMAGE("og:image");

  private final String name;

  OgTagType(String s) {
    this.name = s;
  }

  static OgTagType fromName(String name) {
    return Stream.of(OgTagType.values())
        .filter(ogTagName -> name.equals(ogTagName.name))
        .findAny()
        .orElse(null);
  }

}
