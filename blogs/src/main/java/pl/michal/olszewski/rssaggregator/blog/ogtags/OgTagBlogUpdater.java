package pl.michal.olszewski.rssaggregator.blog.ogtags;

import static java.util.stream.Stream.of;
import static pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagType.DESCRIPTION;
import static pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagType.IMAGE;
import static pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagType.TITLE;
import static pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagType.fromName;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;

@Slf4j
@Service
class OgTagBlogUpdater {

  private static final String META = "meta";
  private static final String PROPERTY = "property";
  private static final String CONTENT = "content";
  private final PageInfoExtractor pageInfoExtractor;

  OgTagBlogUpdater(PageInfoExtractor pageInfoExtractor) {
    this.pageInfoExtractor = pageInfoExtractor;
  }

  Blog updateBlogByOgTagInfo(Blog blog) {
    OgTagBlogInfo blogInfo = getBlogInfoFromMetaTags(blog.getBlogURL());
    if (blogInfo != null) {
      blog.updateBlogByOgTagInfo(blogInfo);
    }
    return blog;
  }

  OgTagBlogInfo getBlogInfoFromMetaTags(String url) {
    return Optional.ofNullable(pageInfoExtractor.getPageInfoFromUrl(url))
        .map(document -> of(document.getElementsByTag(META))
            .flatMap(Collection::stream)
            .filter(element -> fromName(element.attr(PROPERTY)) != null)
            .collect(Collectors.toMap(
                element -> fromName(element.attr(PROPERTY)),
                element -> element.attr(CONTENT),
                (a1, a2) -> a1
            )))
        .map(ogTagTypeMap -> new OgTagBlogInfo(
            ogTagTypeMap.getOrDefault(TITLE, ""),
            ogTagTypeMap.getOrDefault(DESCRIPTION, ""),
            ogTagTypeMap.getOrDefault(IMAGE, "")
        )).orElse(null);
  }

}
