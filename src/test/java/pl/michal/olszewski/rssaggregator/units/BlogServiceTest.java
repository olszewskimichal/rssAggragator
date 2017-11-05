package pl.michal.olszewski.rssaggregator.units;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.persistence.PersistenceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;
import pl.michal.olszewski.rssaggregator.service.BlogService;

public class BlogServiceTest {

  private BlogService blogService;

  @Mock
  private BlogRepository blogRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    blogService = new BlogService(blogRepository);
  }

  @Test
  public void shouldCreateBlogFromDTO() {
    //given
    BlogDTO blogDTO = BlogDTO.builder().build();
    //when
    Blog blog = blogService.createBlog(blogDTO);
    //then
    assertThat(blog).isNotNull();
  }

  @Test
  public void shouldCreateBlogWithCorrectProperties() {
    //given
    Instant now = Instant.now();
    BlogDTO blogDTO = BlogDTO.builder().name("nazwa").description("desc").feedURL("url").link("blogUrl").publishedDate(now).build();  //TODO serwis do czasu
    //when
    Blog blog = blogService.createBlog(blogDTO);
    //then
    assertThat(blog).isNotNull();
    assertThat(blog.getDescription()).isEqualTo("desc");
    assertThat(blog.getName()).isEqualTo("nazwa");
    assertThat(blog.getFeedURL()).isEqualTo("url");
    assertThat(blog.getBlogURL()).isEqualTo("blogUrl");
    assertThat(blog.getPublishedDate()).isAfterOrEqualTo(now).isBeforeOrEqualTo(now);
  }

  @Test
  public void shouldPersistBlogOnCreate() {
    //given
    BlogDTO blogDTO = BlogDTO.builder().build();
    //when
    Blog blog = blogService.createBlog(blogDTO);
    //then
    assertThat(blog).isNotNull();
    Mockito.verify(blogRepository, Mockito.times(1)).save(blog);
  }

  @Test
  public void shouldNotCreateBlogWhenThrowException() {
    //given
    BlogDTO blogDTO = BlogDTO.builder().build();
    given(blogRepository.save(Matchers.any(Blog.class))).willThrow(new PersistenceException("Blog o podanym url juz istnieje"));
    //when
    //then
    assertThatThrownBy(() -> blogService.createBlog(blogDTO)).isNotNull().hasMessage("Blog o podanym url juz istnieje");
  }

  @Test
  public void shouldCreateBlogWith2Items() {
    //given
    List<ItemDTO> itemsList = IntStream.rangeClosed(1, 2).mapToObj(v -> ItemDTO.builder().title("title" + v).build()).collect(Collectors.toList());
    BlogDTO blogDTO = BlogDTO.builder().itemsList(itemsList).build();
    //when
    Blog blog = blogService.createBlog(blogDTO);
    //then
    assertThat(blog).isNotNull();
    assertThat(blog.getItems()).isNotEmpty().hasSize(2);
  }

  @Test
  public void shouldCreateItemsWithCorrectProperties() {
    Instant now = Instant.now();
    List<ItemDTO> itemsList = IntStream.rangeClosed(1, 2).mapToObj(v -> ItemDTO.builder().author("autor").date(now).description("desc").title(v + "").link("link" + v).build())
        .collect(Collectors.toList());
    BlogDTO blogDTO = BlogDTO.builder().itemsList(itemsList).build();
    //when
    Blog blog = blogService.createBlog(blogDTO);
    //then
    assertThat(blog.getItems()).isNotEmpty().hasSize(2);
    for (Item item : blog.getItems()) {
      assertThat(item.getAuthor()).isEqualTo("autor");
      assertThat(item.getBlog()).isEqualTo(blog);
      assertThat(item.getDate()).isBeforeOrEqualTo(now).isAfterOrEqualTo(now);
      assertThat(item.getTitle()).isNotNull().isNotEmpty();
      assertThat(item.getLink()).isEqualTo("link" + item.getTitle());
    }
  }

  @Test
  public void shouldUpdateBlogWhenNewItemAdd() {
    //given
    Blog blog = new Blog("url", "", "", "", null);
    List<ItemDTO> itemsList = IntStream.rangeClosed(1, 1).mapToObj(v -> ItemDTO.builder().author("autor").description("desc").title(v + "").link("link" + v).build()).collect(Collectors.toList());
    BlogDTO blogDTO = BlogDTO.builder().link("url").itemsList(itemsList).build();
    given(blogRepository.findByBlogURL("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToIgnoringGivenFields(blog, "items");
    assertThat(updateBlog.getItems()).isNotEmpty().hasSize(1);
  }

  @Test
  public void shouldAddItemForBlogWhichHaveOneItem() {
    //given
    Blog blog = new Blog("url", "", "", "", null);
    blog.addItem(new Item(ItemDTO.builder().title("title").build()));
    List<ItemDTO> itemsList = IntStream.rangeClosed(2, 2).mapToObj(v -> ItemDTO.builder().author("autor").description("desc").title(v + "").link("link" + v).build()).collect(Collectors.toList());
    BlogDTO blogDTO = BlogDTO.builder().link("url").itemsList(itemsList).build();
    given(blogRepository.findByBlogURL("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToIgnoringGivenFields(blog, "items");
    assertThat(updateBlog.getItems()).isNotEmpty().hasSize(2);
  }

  @Test
  public void shouldNotAddItemWhenIsTheSame() {
    //given
    ItemDTO itemDTO = ItemDTO.builder().title("title").build();
    Blog blog = new Blog("url", "", "", "", null);
    blog.addItem(new Item(itemDTO));
    BlogDTO blogDTO = BlogDTO.builder().link("url").itemsList(Arrays.asList(itemDTO, itemDTO)).build();
    given(blogRepository.findByBlogURL("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToComparingFieldByField(blog);
  }

  @Test
  public void shouldNotUpdateBlogWhenNothingChanged() {
    //given
    Blog blog = new Blog("url", "", "", "", null);
    BlogDTO blogDTO = BlogDTO.builder().link("url").build();
    given(blogRepository.findByBlogURL("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToComparingFieldByField(blog);
  }

  @Test
  public void shouldUpdateBlogWhenDescriptionChanged() {
    //given
    Blog blog = new Blog("url", "", "", "", null);
    BlogDTO blogDTO = BlogDTO.builder().link("url").description("desc").name("").feedURL("").build();
    given(blogRepository.findByBlogURL("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToIgnoringGivenFields(blog, "description");
    assertThat(updateBlog.getDescription()).isEqualTo("desc");
  }

  @Test
  public void shouldGetBlogByURL() {
    //given
    given(blogRepository.findByBlogURL("url")).willReturn(Optional.of(new Blog("", "", "", "", null)));
    //when
    Blog byURL = blogService.getBlogByURL("url");
    //then
    assertThat(byURL).isNotNull();
  }

  @Test
  public void shouldThrownExceptionWhenBlogByURLNotExist() {
    //given
    given(blogRepository.findByBlogURL("url")).willReturn(Optional.empty());
    //when
    assertThatThrownBy(() -> blogService.getBlogByURL("url")).isNotNull().hasMessage("Nie znaleziono blogu o URL = url");
  }

  @Test
  public void shouldGetBlogById() {
    //given
    given(blogRepository.findById(1L)).willReturn(Optional.of(new Blog("", "", "", "", null)));
    //when
    Blog blogById = blogService.getBlogById(1L);
    //then
    assertThat(blogById).isNotNull();
  }

  @Test
  public void shouldThrownExceptionWhenBlogByIdNotExist() {
    //given
    given(blogRepository.findById(1L)).willReturn(Optional.empty());
    //when
    assertThatThrownBy(() -> blogService.getBlogById(1L)).isNotNull().hasMessage("Nie znaleziono bloga o id = 1");
  }

  @Test
  public void shouldDeleteBlogById() {
    given(blogRepository.findById(1L)).willReturn(Optional.of(new Blog("", "", "", "", null)));

    assertThat(blogService.deleteBlog(1L)).isTrue();
  }

  @Test
  public void shouldThrowExceptionOnDeleteWhenBlogNotExist() {
    given(blogRepository.findById(1L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> blogService.deleteBlog(1L)).isNotNull().hasMessage("Nie znaleziono bloga o id = 1");
  }

  @Test
  public void shouldGetBlogDTOById() {
    //given
    given(blogRepository.findById(1L)).willReturn(Optional.of(new Blog("", "", "", "", null)));
    //when
    BlogDTO blogById = blogService.getBlogDTOById(1L);
    //then
    assertThat(blogById).isNotNull();
  }

  @Test
  public void shouldThrownExceptionWhenBlogDTOByIdNotExist() {
    //given
    given(blogRepository.findById(1L)).willReturn(Optional.empty());
    //when
    assertThatThrownBy(() -> blogService.getBlogDTOById(1L)).isNotNull().hasMessage("Nie znaleziono bloga o id = 1");
  }

  @Test
  public void shouldGetEmptyBlogs() {
    //given
    given(blogRepository.findStreamAll()).willReturn(Stream.empty());
    //when
    List<Blog> blogs = blogService.getAllBlogs();
    //then
    assertThat(blogs).isNotNull().isEmpty();
  }

  @Test
  public void shouldGetAllBlogs() {
    //given
    given(blogRepository.findStreamAll()).willReturn(Stream.of(new Blog()));
    //when
    List<Blog> blogs = blogService.getAllBlogs();
    //then
    assertThat(blogs).isNotNull().isNotEmpty().hasSize(1);
  }

  @Test
  public void shouldGetEmptyBlogsDTOs() {
    //given
    given(blogRepository.findAll(new PageRequest(0, 20))).willReturn(new PageImpl<>(Collections.emptyList()));
    //when
    List<BlogDTO> blogs = blogService.getAllBlogDTOs(null, null);
    //then
    assertThat(blogs).isNotNull().isEmpty();
  }

  @Test
  public void shouldGetAllBlogDTOs() {
    //given
    given(blogRepository.findAll(new PageRequest(0, 20))).willReturn(new PageImpl<>(Collections.singletonList(new Blog())));
    //when
    List<BlogDTO> blogs = blogService.getAllBlogDTOs(null, null);
    //then
    assertThat(blogs).isNotNull().isNotEmpty().hasSize(1);
  }
}
