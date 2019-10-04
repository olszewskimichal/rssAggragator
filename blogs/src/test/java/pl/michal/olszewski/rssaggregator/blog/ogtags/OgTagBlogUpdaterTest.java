package pl.michal.olszewski.rssaggregator.blog.ogtags;

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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OgTagBlogUpdaterTest {

  @Mock
  private MongoTemplate mongoTemplate;

  private OgTagBlogUpdater updater;

  @BeforeEach
  void setUp() {
    given(mongoTemplate.save(any(Blog.class))).willAnswer(i -> i.getArgument(0));
    updater = new OgTagBlogUpdater(new InMemoryPageInfoExtractor(), mongoTemplate);
  }

  @Test
  void shouldUpdateBlogByOgTag() {
    //given
    Blog blog = Blog.builder().blogURL("url").build();
    //when
    updater.updateBlogByOgTagInfo(blog);
    //then
    assertThat(blog.getDescription()).isEqualTo("description");
    assertThat(blog.getName()).isEqualTo("title");
    assertThat(blog.getImageUrl()).isEqualTo("imageUrl");
  }

  @Test
  void shouldOnlyUpdateImageUrlByOgTagWhenBlogHasInfoAboutDescriptionAndName() {
    //given
    Blog blog = Blog.builder().description("desc").name("name").blogURL("url").build();
    //when
    updater.updateBlogByOgTagInfo(blog);
    //then
    assertThat(blog.getDescription()).isEqualTo("desc");
    assertThat(blog.getName()).isEqualTo("name");
    assertThat(blog.getImageUrl()).isEqualTo("imageUrl");
  }

}