package pl.michal.olszewski.rssaggregator.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.michal.olszewski.rssaggregator.item.Item;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlogServiceTest {

    private BlogService blogService;

    @Mock
    private BlogReactiveRepository blogRepository;

    @Mock
    private MongoTemplate itemRepository;

    @BeforeEach
    void setUp() {
        given(blogRepository.save(any(Blog.class))).willAnswer(i -> Mono.just(i.getArgument(0)));
        given(itemRepository.save(any(Item.class))).willAnswer(i -> Mono.just(i.getArgument(0)));
        blogService = new BlogService(blogRepository, Clock.fixed(Instant.parse("2000-01-01T10:00:55.000Z"), ZoneId.systemDefault()), itemRepository);
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
        Mono<Blog> blog = blogService.createBlog(blogDTO);
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
        Mono<Blog> blog = blogService.createBlog(blogDTO);
        //then
        verify(blogRepository, times(1)).findByFeedURL("nazwa"); //TODO moze jest cos lepszego do mockowania FLuxow i mono
        assertThat(blog).isNotNull();
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
            .build(); //TODO skrocic link
        given(blogRepository.findByFeedURL("feedUrl3")).willReturn(Mono.empty());
        //when
        Mono<Blog> blog = blogService.createBlog(blogDTO);
        //then
        StepVerifier.create(blog)
            .assertNext(v -> assertAll(
                () -> assertThat(v).isNotNull(),
                () -> assertThat(v.getDescription()).isEqualTo("desc"),
                () -> assertThat(v.getName()).isEqualTo("nazwa1"),
                () -> assertThat(v.getFeedURL()).isEqualTo("feedUrl3"),
                () -> assertThat(v.getBlogURL()).isEqualTo("blogUrl1"),
                () -> assertThat(v.getPublishedDate()).isAfterOrEqualTo(now).isBeforeOrEqualTo(now)
            ))
            .expectComplete()
            .verify();

    }

    @Test
    void shouldPersistBlogOnCreate() {
        //given
        given(blogRepository.findByFeedURL("feedUrl5")).willReturn(Mono.empty());
        BlogDTO blogDTO = BlogDTO.builder()
            .feedURL("feedUrl5")
            .build();
        //when
        Mono<Blog> blog = blogService.createBlog(blogDTO);
        //then
        assertThat(blog).isNotNull();
        verify(blogRepository, times(1)).save(blog.block());
    }

    @Test
    void shouldNotCreateBlogWhenThrowException() {
        //given
        BlogDTO blogDTO = BlogDTO.builder().build();
        Mockito.doThrow(new DuplicateKeyException("Blog o podanym url juz istnieje"))
            .when(blogRepository).save(any());
        //when
        //then
        assertThatThrownBy(() -> blogService.createBlog(blogDTO)).isNotNull().hasMessage("Blog o podanym url juz istnieje");
    }

    @Test
    void shouldCreateBlogWith2Items() {
        //given
        given(blogRepository.findByFeedURL("feedUrl2")).willReturn(Mono.empty());
        List<ItemDTO> itemsList = IntStream.rangeClosed(1, 2)
            .mapToObj(v -> ItemDTO.builder().title("title" + v).build())
            .collect(Collectors.toList()); //TODO przerobic linie
        BlogDTO blogDTO = BlogDTO.builder().feedURL("feedUrl2").itemsList(itemsList).build();
        //when
        Mono<Blog> blog = blogService.createBlog(blogDTO);
        //then
        StepVerifier.create(blog)
            .assertNext(v -> {
                assertThat(v).isNotNull();
                assertThat(v.getItems()).isNotEmpty().hasSize(2);
            })
            .expectComplete()
            .verify();
    }

    @Test
    void shouldCreateItemsWithCorrectProperties() {
        Instant now = Instant.now();
        List<ItemDTO> itemsList = IntStream.rangeClosed(1, 2)
            .mapToObj(v -> ItemDTO.builder().author("autor").date(now).description("desc").title(v + "").link("link" + v).build()) //TODO przerobic linie
            .collect(Collectors.toList());
        BlogDTO blogDTO = BlogDTO.builder().feedURL("feedUrl4").itemsList(itemsList).build();
        given(blogRepository.findByFeedURL("feedUrl4")).willReturn(Mono.empty());

        //when
        Mono<Blog> blog = blogService.createBlog(blogDTO);
        //then
        StepVerifier.create(blog)
            .assertNext(v -> {
                assertThat(v.getItems()).isNotEmpty().hasSize(2);
                for (Item item : v.getItems()) {
                    assertThat(item.getAuthor()).isEqualTo("autor");
                    assertThat(item.getDate()).isBeforeOrEqualTo(now).isAfterOrEqualTo(now);
                    assertThat(item.getTitle()).isNotNull().isNotEmpty();
                    assertThat(item.getLink()).isEqualTo("link" + item.getTitle());
                }
            })
            .expectComplete()
            .verify();
    }

    @Test
    void shouldUpdateBlogWhenNewItemAdd() {
        //given
        Blog blog = new Blog("url", "", "url", "", null, null);
        List<ItemDTO> itemsList = IntStream.rangeClosed(1, 1)
            .mapToObj(v -> ItemDTO.builder().date(Instant.now()).author("autor").description("desc").title(v + "").link("link" + v).build()) //przerobic linie
            .collect(Collectors.toList());
        BlogDTO blogDTO = BlogDTO.builder()
            .name("url")
            .feedURL("url")
            .itemsList(itemsList)
            .build();
        given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));
        //when
        Mono<Blog> updateBlog = blogService.updateBlog(blogDTO);
        //then
        StepVerifier.create(updateBlog)
            .assertNext(v -> assertAll(
                () -> assertThat(v).isEqualToIgnoringGivenFields(blog, "items"),
                () -> assertThat(v.getItems()).isNotEmpty().hasSize(1)
            ))
            .expectComplete()
            .verify();

    }

    @Test
    void shouldAddItemForBlogWhichHaveOneItem() {
        //given
        Blog blog = new Blog("url", "", "url", "", null, null);
        blog.addItem(new Item(ItemDTO.builder().title("title").build()), itemRepository);
        List<ItemDTO> itemsList = IntStream.rangeClosed(2, 2)
            .mapToObj(v -> ItemDTO.builder().author("autor").description("desc").date(Instant.now()).title(v + "").link("link" + v).build()) //TODO przerobic linie
            .collect(Collectors.toList());
        BlogDTO blogDTO = BlogDTO.builder().name("url").feedURL("url").itemsList(itemsList).build();
        given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));

        //when
        Mono<Blog> updateBlog = blogService.updateBlog(blogDTO);
        //then
        StepVerifier.create(updateBlog)
            .assertNext(v -> assertAll(
                () -> assertThat(v).isEqualToIgnoringGivenFields(blog, "items"),
                () -> assertThat(v.getItems()).isNotEmpty().hasSize(2)
            ))
            .expectComplete()
            .verify();
    }

    @Test
        //TODO zmniejszyc liczbe linii w sekcji given
    void shouldNotAddItemWhenIsTheSame() {
        //given
        ItemDTO itemDTO = ItemDTO.builder()
            .title("title")
            .date(Instant.now())
            .build();
        Blog blog = new Blog("url", "", "url", "", null, null);
        blog.addItem(new Item(itemDTO), itemRepository);
        BlogDTO blogDTO = BlogDTO.builder()
            .name("url")
            .feedURL("url")
            .itemsList(Arrays.asList(itemDTO, itemDTO))
            .build();
        given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));
        //when
        Mono<Blog> updateBlog = blogService.updateBlog(blogDTO);
        //then
        StepVerifier.create(updateBlog)
            .assertNext(v -> assertThat(v).isEqualToComparingFieldByField(blog))
            .expectComplete()
            .verify();
    }

    @Test
    void shouldNotUpdateBlogWhenNothingChanged() {
        //given
        Blog blog = new Blog("url", "", "url", "", null, null);
        BlogDTO blogDTO = BlogDTO.builder()
            .name("url")
            .feedURL("url")
            .build();
        given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));
        //when
        Mono<Blog> updateBlog = blogService.updateBlog(blogDTO);
        //then
        StepVerifier.create(updateBlog)
            .assertNext(v -> assertThat(v).isEqualToComparingFieldByField(blog))
            .expectComplete()
            .verify();
    }

    @Test
    void shouldUpdateBlogWhenDescriptionChanged() {
        //given
        Blog blog = new Blog("url", "", "url", "", null, null);
        BlogDTO blogDTO = BlogDTO.builder()
            .feedURL("url")
            .description("desc")
            .name("url")
            .build();
        given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));
        //when
        Mono<Blog> updateBlog = blogService.updateBlog(blogDTO);
        //then
        StepVerifier.create(updateBlog)
            .assertNext(v -> assertAll(
                () -> assertThat(v).isEqualToIgnoringGivenFields(blog, "description"),
                () -> assertThat(v.getDescription()).isEqualTo("desc")
            ))
            .expectComplete()
            .verify();
    }

    @Test
    void shouldDeleteBlogById() {
        given(blogRepository.delete(any())).willReturn(Mono.empty());
        given(blogRepository.findById("1")).willReturn(Mono.just(new Blog("", "", "", "", null, null)));

        StepVerifier.create(blogService.deleteBlog("1"))
            .expectComplete()
            .verify();
    }

    @Test
    void shouldThrowExceptionOnDeleteWhenBlogNotExist() {
        given(blogRepository.findById("1")).willReturn(Mono.empty());

        assertThatThrownBy(() -> blogService.deleteBlog("1").block()).isNotNull().hasMessage("Nie znaleziono bloga = 1");
    }

    @Test
    void shouldGetBlogDTOById() {
        //given
        given(blogRepository.findById("1")).willReturn(Mono.just(new Blog("", "", "", "", null, null)));
        //when
        Mono<BlogDTO> blogById = blogService.getBlogDTOById("1");
        //then
        assertThat(blogById).isNotNull();
    }

    @Test
    void shouldThrownExceptionWhenBlogDTOByIdNotExist() {
        //given
        given(blogRepository.findById("1")).willReturn(Mono.empty());
        //when
        StepVerifier.create(blogService.getBlogDTOById("1"))
            .expectErrorMessage("Nie znaleziono bloga = 1")
            .verify();
    }

    @Test
    void shouldGetEmptyBlogs() {
        //given
        given(blogRepository.findAll()).willReturn(Flux.empty());
        //when
        Flux<Blog> blogs = blogService.getAllBlogs();
        //then
        StepVerifier.create(blogs)
            .expectNextCount(0)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldGetAllBlogs() {
        //given
        given(blogRepository.findAll()).willReturn(Flux.just(new Blog()));
        //when
        Flux<Blog> blogs = blogService.getAllBlogs();
        //then
        StepVerifier.create(blogs)
            .expectNextCount(1)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldGetEmptyBlogsDTOs() {
        //given
        given(blogRepository.findAll()).willReturn(Flux.empty());
        //when
        Flux<BlogDTO> blogs = blogService.getAllBlogDTOs(null);
        //then
        StepVerifier.create(blogs)
            .expectNextCount(0)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldGetAllBlogDTOs() {
        //given
        given(blogRepository.findAll()).willReturn(Flux.just(new Blog()));
        //when
        Flux<BlogDTO> blogs = blogService.getAllBlogDTOs(null);
        //then
        StepVerifier.create(blogs)
            .expectNextCount(1)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldGetBlogDTOByName() {
        //given
        given(blogRepository.findByName("name")).willReturn(Mono.just(new Blog("", "", "", "", null, null)));
        //when
        Mono<BlogDTO> blogById = blogService.getBlogDTOByName("name");
        //then
        assertThat(blogById).isNotNull();
    }

    @Test
    void shouldThrownExceptionWhenBlogDTOByNameNotExist() {
        //given
        given(blogRepository.findByName("name")).willReturn(Mono.empty());
        //when
        StepVerifier.create(blogService.getBlogDTOByName("name"))
            .expectErrorMessage("Nie znaleziono bloga = name")
            .verify();
    }

    @Test
    void shouldChangeActivityBlogWhenWeTryDeleteBlogWithItems() {
        Item item = new Item(ItemDTO.builder().link("test").build());
        Blog blog = new Blog("", "", "", "", null, null);
        blog.addItem(item, itemRepository);

        given(blogRepository.findById("1")).willReturn(Mono.just(blog));
        //when
        blogService.deleteBlog("1").block();
        //then
        assertThat(blog.isActive()).isFalse();
    }

}
