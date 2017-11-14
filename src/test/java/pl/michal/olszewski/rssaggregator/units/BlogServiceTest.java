package pl.michal.olszewski.rssaggregator.units;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.dto.ItemDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.entity.Item;
import pl.michal.olszewski.rssaggregator.extenstions.MockitoExtension;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;
import pl.michal.olszewski.rssaggregator.service.BlogService;


@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

  private BlogService blogService;

  @Mock
  private BlogRepository blogRepository;

  @BeforeEach
  public void setUp() {
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
    verify(blogRepository, times(1)).save(blog);
  }

  @Test
  public void shouldNotCreateBlogWhenThrowException() {
    //given
    BlogDTO blogDTO = BlogDTO.builder().build();
    given(blogRepository.save(any(Blog.class))).willThrow(new PersistenceException("Blog o podanym url juz istnieje"));
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
    Blog blog = new Blog("url", "", "url", "", null);
    List<ItemDTO> itemsList = IntStream.rangeClosed(1, 1).mapToObj(v -> ItemDTO.builder().author("autor").description("desc").title(v + "").link("link" + v).build()).collect(Collectors.toList());
    BlogDTO blogDTO = BlogDTO.builder().name("url").itemsList(itemsList).build();
    given(blogRepository.findByName("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToIgnoringGivenFields(blog, "items");
    assertThat(updateBlog.getItems()).isNotEmpty().hasSize(1);
  }

  @Test
  public void shouldAddItemForBlogWhichHaveOneItem() {
    //given
    Blog blog = new Blog("url", "", "url", "", null);
    blog.addItem(new Item(ItemDTO.builder().title("title").build()));
    List<ItemDTO> itemsList = IntStream.rangeClosed(2, 2).mapToObj(v -> ItemDTO.builder().author("autor").description("desc").title(v + "").link("link" + v).build()).collect(Collectors.toList());
    BlogDTO blogDTO = BlogDTO.builder().name("url").link("url").itemsList(itemsList).build();
    given(blogRepository.findByName("url")).willReturn(Optional.of(blog));
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
    Blog blog = new Blog("url", "", "url", "", null);
    blog.addItem(new Item(itemDTO));
    BlogDTO blogDTO = BlogDTO.builder().name("url").link("url").itemsList(Arrays.asList(itemDTO, itemDTO)).build();
    given(blogRepository.findByName("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToComparingFieldByField(blog);
  }

  @Test
  public void shouldNotUpdateBlogWhenNothingChanged() {
    //given
    Blog blog = new Blog("url", "", "url", "", null);
    BlogDTO blogDTO = BlogDTO.builder().name("url").link("url").build();
    given(blogRepository.findByName("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToComparingFieldByField(blog);
  }

  @Test
  public void shouldUpdateBlogWhenDescriptionChanged() {
    //given
    Blog blog = new Blog("url", "", "url", "", null);
    BlogDTO blogDTO = BlogDTO.builder().link("url").description("desc").name("url").feedURL("").build();
    given(blogRepository.findByName("url")).willReturn(Optional.of(blog));
    //when
    Blog updateBlog = blogService.updateBlog(blogDTO);
    //then
    assertThat(updateBlog).isEqualToIgnoringGivenFields(blog, "description");
    assertThat(updateBlog.getDescription()).isEqualTo("desc");
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
    given(blogRepository.findStreamAll()).willReturn(Stream.empty());
    //when
    List<BlogDTO> blogs = blogService.getAllBlogDTOs(null);
    //then
    assertThat(blogs).isNotNull().isEmpty();
  }

  @Test
  public void shouldGetAllBlogDTOs() {
    //given
    given(blogRepository.findStreamAll()).willReturn(Stream.of(new Blog()));
    //when
    List<BlogDTO> blogs = blogService.getAllBlogDTOs(null);
    //then
    assertThat(blogs).isNotNull().isNotEmpty().hasSize(1);
  }

  @Test
  public void shouldGetBlogDTOByName() {
    //given
    given(blogRepository.findByName("name")).willReturn(Optional.of(new Blog("", "", "", "", null)));
    //when
    BlogDTO blogById = blogService.getBlogDTOByName("name");
    //then
    assertThat(blogById).isNotNull();
  }

  @Test
  public void shouldThrownExceptionWhenBlogDTOByNameNotExist() {
    //given
    given(blogRepository.findByName("name")).willReturn(Optional.empty());
    //when
    assertThatThrownBy(() -> blogService.getBlogDTOByName("name")).isNotNull().hasMessage("Nie znaleziono blogu = name");
  }
}
