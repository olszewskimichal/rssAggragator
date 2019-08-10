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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jms.core.JmsTemplate;
import pl.michal.olszewski.rssaggregator.blog.newitem.NewItemInBlogEventProducer;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEventProducer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlogServiceTest {

  private BlogService blogService;

  @Mock
  private BlogFinder blogFinder;

  @Mock
  private BlogUpdater blogUpdater;

  @Mock
  private JmsTemplate jmsTemplate;

  @Mock
  private BlogReactiveRepository blogRepository;

  @BeforeEach
  void setUp() {
    given(blogRepository.save(any(Blog.class))).willAnswer(i -> {
          Blog argument = i.getArgument(0);
          argument.setId(UUID.randomUUID().toString());
          return Mono.just(argument);
        }
    );
    blogService = new BlogService(
        new BlogFinder(blogRepository),
        new BlogUpdater(blogRepository),
        Caffeine.newBuilder().build(),
        Caffeine.newBuilder().build(),
        new NewItemInBlogEventProducer(jmsTemplate),
        new NewItemForSearchEventProducer(jmsTemplate)
    );
    blogService.evictBlogCache();
  }

  @Test
  void shouldCreateBlogFromDTO() {
    //given
    given(blogRepository.findByFeedURL("feedUrl1")).willReturn(Mono.empty());
    BlogDTO blogDTO = BlogDTO.builder()
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

    BlogDTO blogDTO = BlogDTO.builder()
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
    BlogDTO blogDTO = BlogDTO.builder()
        .name("nazwa1")
        .description("desc")
        .feedURL("feedUrl3")
        .link("blogUrl1")
        .publishedDate(now)
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
            () -> assertThat(dto.getLink()).isEqualTo("blogUrl1"),
            () -> assertThat(dto.getPublishedDate()).isAfterOrEqualTo(now).isBeforeOrEqualTo(now)
        ))
        .expectComplete()
        .verify();

  }

  @Test
  void shouldUpdateBlogWhenNewItemAdd() {
    //given
    Blog blog = Blog.builder().id(UUID.randomUUID().toString()).feedURL("url").name("url").build();
    given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));

    List<ItemDTO> itemsList = IntStream.rangeClosed(1, 1)
        .mapToObj(number -> ItemDTO.builder().date(Instant.now()).author("autor").description("desc").title(number + "").link("link" + number).build()) //TODO
        .collect(Collectors.toList());

    BlogDTO blogDTO = BlogDTO.builder()
        .name("url")
        .feedURL("url")
        .itemsList(itemsList)
        .build();

    //when
    Mono<BlogDTO> updateBlog = blogService.updateBlog(blogDTO);

    //then
    StepVerifier.create(updateBlog)
        .assertNext(dto -> assertThat(dto).isEqualToIgnoringGivenFields(blogDTO, "itemsList"))
        .expectComplete()
        .verify();
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(NewItemInBlogEvent.class));
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(NewItemForSearchEvent.class));
  }

  @Test
  void shouldAddItemToBlogOnlyOnce() {
    //given
    ItemDTO itemDTO = ItemDTO.builder()
        .title("title")
        .link("url")
        .date(Instant.now())
        .build();

    Blog blog = Blog.builder()
        .id(UUID.randomUUID().toString())
        .feedURL("url")
        .name("url")
        .build();

    BlogDTO blogDTO = BlogDTO.builder()
        .name("url")
        .feedURL("url")
        .itemsList(Arrays.asList(itemDTO, itemDTO))
        .build();
    given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));

    //when
    Mono<BlogDTO> updateBlog = blogService.updateBlog(blogDTO);

    //then
    StepVerifier.create(updateBlog)
        .expectNextCount(1)
        .expectComplete()
        .verify();
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(NewItemInBlogEvent.class));
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(NewItemForSearchEvent.class));
  }

  @Test
  void shouldUpdateBlogWhenDescriptionChanged() {
    //given
    Blog blog = Blog.builder().id(UUID.randomUUID().toString()).feedURL("url").name("url").build();

    BlogDTO blogDTO = BlogDTO.builder()
        .feedURL("url")
        .description("desc")
        .name("url")
        .build();
    given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));

    //when
    Mono<BlogDTO> updateBlog = blogService.updateBlog(blogDTO);

    //then
    StepVerifier.create(updateBlog)
        .assertNext(dto -> assertAll(
            () -> assertThat(dto).isEqualToIgnoringGivenFields(blogDTO, "description"),
            () -> assertThat(dto.getDescription()).isEqualTo("desc")
        ))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldDeleteBlogById() {
    given(blogRepository.deleteById("1")).willReturn(Mono.empty());
    given(blogRepository.getBlogWithCount("1")).willReturn(Mono.just(new BlogAggregationDTO("1", BlogDTO.builder().build())));

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
    BlogDTO blogDTO = BlogDTO.builder()
        .item(ItemDTO.builder().link("test").blogId("1").build())
        .build();
    BlogAggregationDTO aggregationDTO = new BlogAggregationDTO("1", blogDTO);
    given(blogRepository.getBlogWithCount("1")).willReturn(Mono.just(aggregationDTO));

    //when
    blogService.deleteBlog("1").block();

    //then
    assertThat(blog.isActive()).isFalse();
  }
}
