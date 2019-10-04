package pl.michal.olszewski.rssaggregator.blog.ogtags;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pl.michal.olszewski.rssaggregator.blog.Blog;

class OgTagBlogUpdaterTest {

  private OgTagBlogUpdater updater = new OgTagBlogUpdater(new InMemoryPageInfoExtractor());

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