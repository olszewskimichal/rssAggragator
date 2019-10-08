package pl.michal.olszewski.rssaggregator.ogtags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogBuilder;
import pl.michal.olszewski.rssaggregator.item.ItemDTOBuilder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OgTagInfoUpdaterTest {

  @Mock
  private MongoTemplate mongoTemplate;

  private OgTagInfoUpdater updater;

  @BeforeEach
  void setUp() {
    given(mongoTemplate.save(any(Blog.class))).willAnswer(i -> i.getArgument(0));
    updater = new OgTagInfoUpdater(new InMemoryPageInfoExtractor(), mongoTemplate);
  }

  @Test
  void shouldUpdateBlogByOgTag() {
    //given
    Blog blog = new BlogBuilder().blogURL("url").build();
    //when
    updater.updateItemByOgTagInfo(blog);
    //then
    assertThat(blog.getDescription()).isEqualTo("description");
    assertThat(blog.getName()).isEqualTo("title");
    assertThat(blog.getImageUrl()).isEqualTo("imageUrl");
  }

  @Test
  void shouldOnlyUpdateImageUrlByOgTagWhenBlogHasInfoAboutDescriptionAndName() {
    //given
    Blog blog = new BlogBuilder().description("desc").name("name").blogURL("url").build();
    //when
    updater.updateItemByOgTagInfo(blog);
    //then
    assertThat(blog.getDescription()).isEqualTo("desc");
    assertThat(blog.getName()).isEqualTo("name");
    assertThat(blog.getImageUrl()).isEqualTo("imageUrl");
  }

  @Test
  void shouldUpdateItemByOgTag() {
    //given
    var itemDTO = new ItemDTOBuilder().link("url").build();
    //when
    var result = updater.updateItemByOgTagInfo(itemDTO);
    //then
    assertThat(itemDTO.getImageURL()).isNull();
    assertThat(result.getImageURL()).isEqualTo("imageUrl");
  }

}