package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlogServiceTest {

  private BlogService blogService;

  @Mock
  private BlogReactiveRepository blogRepository;

  @Mock
  private BlogSyncRepository blogSyncRepository;

  @BeforeEach
  void setUp() {
    given(blogRepository.save(any(Blog.class))).willAnswer(i -> {
          Blog argument = i.getArgument(0);
          argument.setId(UUID.randomUUID().toString());
          return Mono.just(argument);
        }
    );
    blogService = new BlogService(
        new BlogFinder(blogRepository, blogSyncRepository),
        new BlogWorker(blogRepository),
        Caffeine.newBuilder().build(),
        new InMemoryBlogValidation());
    blogService.evictBlogCache();
  }

  @Test
  void shouldCreateBlogFromDTO() {
    //given
    given(blogRepository.findByFeedURL("feedUrl1")).willReturn(Mono.empty());
    CreateBlogDTO blogDTO = CreateBlogDTO.builder()
        .feedURL("feedUrl1")
        .name("test")
        .build();

    //when
    Mono<BlogDTO> blog = blogService.getBlogOrCreate(blogDTO);

    //then
    assertThat(blog).isNotNull();
  }

  @Test
  void shouldNotTryCreatingBlogWhenExist() {
    //given
    given(blogRepository.findByFeedURL("nazwa")).willReturn(Mono.just(new Blog()));

    CreateBlogDTO blogDTO = CreateBlogDTO.builder()
        .feedURL("nazwa")
        .build();

    //when
    Mono<BlogDTO> blog = blogService.getBlogOrCreate(blogDTO);

    //then
    assertThat(blog).isNotNull();
    verify(blogRepository, times(0)).save(any(Blog.class));
  }

  @Test
  void shouldCreateBlogWithCorrectProperties() {
    //given
    Instant now = Instant.now();
    CreateBlogDTO blogDTO = CreateBlogDTO.builder()
        .name("nazwa1")
        .description("desc")
        .feedURL("feedUrl3")
        .link("blogUrl1")
        .build();
    given(blogRepository.findByFeedURL("feedUrl3")).willReturn(Mono.empty());

    //when
    Mono<BlogDTO> blog = blogService.getBlogOrCreate(blogDTO);

    //then
    StepVerifier.create(blog)
        .assertNext(dto -> assertAll(
            () -> assertThat(dto).isNotNull(),
            () -> assertThat(dto.getDescription()).isEqualTo("desc"),
            () -> assertThat(dto.getName()).isEqualTo("nazwa1"),
            () -> assertThat(dto.getFeedURL()).isEqualTo("feedUrl3"),
            () -> assertThat(dto.getLink()).isEqualTo("blogUrl1")
        ))
        .expectComplete()
        .verify();

  }

  @Test
  void shouldUpdateBlogWhenDescriptionChanged() {
    //given
    Blog blog = Blog.builder().id(UUID.randomUUID().toString()).feedURL("url").name("url").build();

    UpdateBlogDTO blogDTO = UpdateBlogDTO.builder()
        .feedURL("url")
        .description("desc")
        .name("url")
        .build();
    given(blogRepository.findById("id")).willReturn(Mono.just(blog));

    //when
    Mono<BlogDTO> updateBlog = blogService.updateBlog(blogDTO, "id");

    //then
    StepVerifier.create(updateBlog)
        .assertNext(dto -> assertAll(
            () -> assertThat(dto.getName()).isEqualTo("url"),
            () -> assertThat(dto.getFeedURL()).isEqualTo("url"),
            () -> assertThat(dto.getDescription()).isEqualTo("desc")
        ))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldDeleteBlogById() {
    given(blogRepository.deleteById("1")).willReturn(Mono.empty());
    given(blogRepository.getBlogWithCount("1")).willReturn(Mono.just(BlogAggregationDTO.builder().blogId("1").blogItemsCount(0L).build()));

    StepVerifier.create(blogService.deleteBlog("1"))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldThrowExceptionOnDeleteWhenBlogNotExist() {
    given(blogRepository.getBlogWithCount("1")).willReturn(Mono.empty());

    assertThatThrownBy(() -> blogService.deleteBlog("1").block()).isNotNull().hasMessage("Nie znaleziono bloga = 1");
  }


  @Test
  void shouldChangeActivityBlogWhenWeTryDeleteBlogWithItems() {
    Blog blog = Blog.builder()
        .build();
    given(blogRepository.findById("1")).willReturn(Mono.just(blog));
    BlogAggregationDTO aggregationDTO = BlogAggregationDTO.builder().blogItemsCount(1L).build();
    given(blogRepository.getBlogWithCount("1")).willReturn(Mono.just(aggregationDTO));

    //when
    blogService.deleteBlog("1").block();

    //then
    assertThat(blog.isActive()).isFalse();
  }
}
