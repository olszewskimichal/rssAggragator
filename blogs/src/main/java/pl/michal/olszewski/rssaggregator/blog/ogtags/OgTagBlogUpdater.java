package pl.michal.olszewski.rssaggregator.blog.ogtags;

import static pl.michal.olszewski.rssaggregator.blog.ogtags.OgTagType.fromName;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogFinder;

class OgTagBlogUpdater {

  private final BlogFinder blogFinder;

  OgTagBlogUpdater(BlogFinder blogFinder) {
    this.blogFinder = blogFinder;
  }

  void updateBlog(String blogId) {
    blogFinder.findByIdSync(blogId)
        .ifPresent(this::updateBlogByOgTagInfo);
  }

  OgTagBlogInfo getBlogInfoFromMetaTags(String url) {
    try {
      Document document = Jsoup.connect(url).userAgent("myUserAgent").get();
      Map<OgTagType, String> collect = Stream.of(document.getElementsByTag("meta"))
          .flatMap(Collection::stream)
          .filter(element -> fromName(element.attr("property")) != null)
          .collect(Collectors.toMap(
              element -> fromName(element.attr("property")),
              element -> element.attr("content")
          ));

      return new OgTagBlogInfo(
          collect.getOrDefault(OgTagType.TITLE, ""),
          collect.getOrDefault(OgTagType.DESCRIPTION, ""),
          collect.getOrDefault(OgTagType.IMAGE, "")
      );
    } catch (IOException ex) {
      //TODO
      return null;
    }
  }

  private Blog updateBlogByOgTagInfo(Blog blog) {
    OgTagBlogInfo blogInfo = getBlogInfoFromMetaTags(blog.getBlogURL());
    blog.updateBlogByOgTagInfo(blogInfo);
    return blog;
  }

}
