package pl.michal.olszewski.rssaggregator.blog.ogtags;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
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

  void updateBlogByOgTagInfo(Blog blog) {
    OgTagBlogInfo blogInfo = getBlogInfoFromMetaTags(blog.getBlogURL());
    blog.updateBlogByOgTagInfo(blogInfo);
  }

  OgTagBlogInfo getBlogInfoFromMetaTags(String url) {
    try {
      Document document = pageInfoExtractor.getPageInfoFromUrl(url);
      Map<OgTagType, String> collect = Stream.of(document.getElementsByTag(OgTagBlogUpdater.META))
          .flatMap(Collection::stream)
          .filter(element -> OgTagType.fromName(element.attr(OgTagBlogUpdater.PROPERTY)) != null)
          .collect(Collectors.toMap(
              element -> OgTagType.fromName(element.attr(OgTagBlogUpdater.PROPERTY)),
              element -> element.attr(OgTagBlogUpdater.CONTENT)
          ));

      return new OgTagBlogInfo(
          collect.getOrDefault(OgTagType.TITLE, ""),
          collect.getOrDefault(OgTagType.DESCRIPTION, ""),
          collect.getOrDefault(OgTagType.IMAGE, "")
      );
    } catch (IOException ex) {
      log.warn("Nie mogę pobrać OG:Tagów z bloga {}", url);
      return null;
    }
  }

}
