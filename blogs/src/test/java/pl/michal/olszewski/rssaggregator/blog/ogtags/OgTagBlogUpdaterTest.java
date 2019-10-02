package pl.michal.olszewski.rssaggregator.blog.ogtags;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

class OgTagBlogUpdaterTest {

  @Test
  void test4() throws IOException {
    Document document = Jsoup.connect("https://devstyle.pl").userAgent("myUserAgent").get();

    Map<OgTagType, String> collect = Stream.of(document.getElementsByTag("meta"))
        .flatMap(Collection::stream)
        .filter(element -> OgTagType.fromName(element.attr("property")) != null)
        .collect(Collectors.toMap(
            element -> OgTagType.fromName(element.attr("property")),
            element -> element.attr("content")
        ));

    OgTagBlogInfo ogTagBlogInfo = new OgTagBlogInfo(
        collect.getOrDefault(OgTagType.TITLE, ""),
        collect.getOrDefault(OgTagType.DESCRIPTION, ""),
        collect.getOrDefault(OgTagType.IMAGE, "")
    );

    System.err.println(ogTagBlogInfo);
  }
}