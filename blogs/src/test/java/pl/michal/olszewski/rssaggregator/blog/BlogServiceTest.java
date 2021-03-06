package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pl.michal.olszewski.rssaggregator.ogtags.OgTagInfoUpdater;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlogServiceTest {

  private BlogService blogService;

  @Mock
  private BlogRepository blogRepository;

  @Mock
  private OgTagInfoUpdater ogTagInfoUpdater;

  @BeforeEach
  void setUp() {
    given(ogTagInfoUpdater.updateItemByOgTagInfo(any(Blog.class)))
        .willAnswer(i -> i.getArgument(0));
    given(blogRepository.save(any(Blog.class))).willAnswer(i -> {
          Blog argument = i.getArgument(0);
          argument.setId(UUID.randomUUID().toString());
          return argument;
        }
    );
    blogService = new BlogService(
        new BlogFinder(blogRepository),
        new BlogWorker(blogRepository),
        Caffeine.newBuilder().build(),
        (blogUrl, feedUrl) -> {
        },
        ogTagInfoUpdater);
  }

  @Test
  void shouldCreateBlogFromDTO() {
    //given
    given(blogRepository.findByFeedURL("feedUrl1")).willReturn(Optional.empty());
    CreateBlogDTO blogDTO = new CreateBlogDTOBuilder()
        .feedURL("feedUrl1")
        .name("test")
        .build();

    //when
    BlogDTO blog = blogService.getBlogOrCreate(blogDTO);

    //then
    assertThat(blog).isNotNull();
  }

  @Test
  void shouldNotTryCreatingBlogWhenExist() {
    //given
    given(blogRepository.findByFeedURL("nazwa")).willReturn(Optional.of(new BlogBuilder().build()));

    CreateBlogDTO blogDTO = new CreateBlogDTOBuilder()
        .feedURL("nazwa")
        .build();

    //when
    BlogDTO blog = blogService.getBlogOrCreate(blogDTO);

    //then
    assertThat(blog).isNotNull();
    verify(blogRepository, times(0)).save(any(Blog.class));
  }

  @Test
  void shouldCreateBlogWithCorrectProperties() {
    //given
    CreateBlogDTO blogDTO = new CreateBlogDTOBuilder()
        .name("nazwa1")
        .description("desc")
        .feedURL("feedUrl3")
        .link("blogUrl1")
        .build();
    given(blogRepository.findByFeedURL("feedUrl3")).willReturn(Optional.empty());

    //when
    BlogDTO blog = blogService.getBlogOrCreate(blogDTO);

    //then
    assertAll(
        () -> assertThat(blog).isNotNull(),
        () -> assertThat(blog.getDescription()).isEqualTo("desc"),
        () -> assertThat(blog.getName()).isEqualTo("nazwa1"),
        () -> assertThat(blog.getFeedURL()).isEqualTo("feedUrl3"),
        () -> assertThat(blog.getLink()).isEqualTo("blogUrl1")
    );
  }

  @Test
  void shouldUpdateBlogWhenDescriptionChanged() {
    //given
    Blog blog = new BlogBuilder().id(UUID.randomUUID().toString()).feedURL("url").name("url").build();

    UpdateBlogDTO blogDTO = new UpdateBlogDTOBuilder()
        .feedURL("url")
        .description("desc")
        .name("url")
        .build();
    given(blogRepository.findById("id")).willReturn(Optional.of(blog));

    //when
    BlogDTO updateBlog = blogService.updateBlog(blogDTO, "id");

    //then
    assertAll(
        () -> assertThat(updateBlog.getName()).isEqualTo("url"),
        () -> assertThat(updateBlog.getFeedURL()).isEqualTo("url"),
        () -> assertThat(updateBlog.getDescription()).isEqualTo("desc")
    );
  }

  @Test
  void shouldThrowExceptionOnDeleteWhenBlogNotExist() {
    given(blogRepository.getBlogWithCount("1")).willReturn(Optional.empty());

    assertThatThrownBy(() -> blogService.deleteBlog("1")).isNotNull().hasMessage("Nie znaleziono bloga = 1");
  }


  @Test
  void shouldChangeActivityBlogWhenWeTryDeleteBlogWithItems() {
    Blog blog = new BlogBuilder().build();
    given(blogRepository.findById("1")).willReturn(Optional.of(blog));
    BlogAggregationDTO aggregationDTO = new BlogAggregationDTOBuilder().blogId("1").blogItemsCount(1L).build();
    given(blogRepository.getBlogWithCount("1")).willReturn(Optional.of(aggregationDTO));

    //when
    blogService.deleteBlog("1");

    //then
    assertThat(blog.isActive()).isFalse();
  }
}
