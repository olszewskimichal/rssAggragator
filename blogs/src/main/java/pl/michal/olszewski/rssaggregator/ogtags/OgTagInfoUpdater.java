package pl.michal.olszewski.rssaggregator.ogtags;

import static java.util.stream.Stream.of;
import static pl.michal.olszewski.rssaggregator.ogtags.OgTagType.DESCRIPTION;
import static pl.michal.olszewski.rssaggregator.ogtags.OgTagType.IMAGE;
import static pl.michal.olszewski.rssaggregator.ogtags.OgTagType.TITLE;
import static pl.michal.olszewski.rssaggregator.ogtags.OgTagType.fromName;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.ItemDTOBuilder;

@Service
public class OgTagInfoUpdater {

  private static final String META = "meta";
  private static final String PROPERTY = "property";
  private static final String CONTENT = "content";
  private final PageInfoExtractor pageInfoExtractor;
  private final MongoTemplate mongoTemplate;

  OgTagInfoUpdater(PageInfoExtractor pageInfoExtractor, MongoTemplate mongoTemplate) {
    this.pageInfoExtractor = pageInfoExtractor;
    this.mongoTemplate = mongoTemplate;
  }

  public Blog updateItemByOgTagInfo(Blog blog) {
    OgTagInfo blogInfo = getBlogInfoFromMetaTags(blog.getBlogURL());
    if (blogInfo != null) {
      blog.updateBlogByOgTagInfo(blogInfo);
    }
    return mongoTemplate.save(blog);
  }

  public ItemDTO updateItemByOgTagInfo(ItemDTO itemDTO) {
    OgTagInfo itemInfo = getBlogInfoFromMetaTags(itemDTO.getLink());
    if (itemInfo != null) {
      return new ItemDTOBuilder().from(itemDTO).imageURL(itemInfo.getImageUrl()).build();
    }
    return itemDTO;
  }

  private OgTagInfo getBlogInfoFromMetaTags(String url) {
    return Optional.ofNullable(pageInfoExtractor.getPageInfoFromUrl(url))
        .map(document -> of(document.getElementsByTag(META))
            .flatMap(Collection::stream)
            .filter(element -> fromName(element.attr(PROPERTY)) != null)
            .collect(Collectors.toMap(
                element -> fromName(element.attr(PROPERTY)),
                element -> element.attr(CONTENT),
                (a1, a2) -> a1
            )))
        .map(ogTagTypeMap -> new OgTagInfo(
            ogTagTypeMap.getOrDefault(TITLE, ""),
            ogTagTypeMap.getOrDefault(DESCRIPTION, ""),
            ogTagTypeMap.getOrDefault(IMAGE, "")
        )).orElse(null);
  }

}
