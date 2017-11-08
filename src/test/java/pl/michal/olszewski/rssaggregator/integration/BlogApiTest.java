package pl.michal.olszewski.rssaggregator.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.michal.olszewski.rssaggregator.assertion.BlogListAssert;
import pl.michal.olszewski.rssaggregator.dto.BlogDTO;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.factory.BlogListFactory;
import pl.michal.olszewski.rssaggregator.repository.BlogRepository;

public class BlogApiTest extends IntegrationTest {

  @Autowired
  private BlogRepository blogRepository;

  @Before
  public void setUp() {
    blogRepository.deleteAll();
  }  //TODO evictCache

  @Test
  public void should_get_empty_list_of_blogs() {
    givenBlog()
        .buildNumberOfBlogsDTOAndSave(0);

    List<BlogDTO> blogs = thenGetBlogsFromApi();

    assertThat(blogs).isEmpty();
  }

  @Test
  public void should_get_all_blogs() {
    givenBlog()
        .buildNumberOfBlogsDTOAndSave(3);

    List<BlogDTO> blogs = thenGetBlogsFromApi();

    assertThat(blogs).hasSize(3);
  }

  @Test
  public void should_get_limit_three_blogs() {
    givenBlog()
        .buildNumberOfBlogsDTOAndSave(6);

    List<BlogDTO> blogDefinitionDTOS = thenGetNumberBlogsFromApi(3);

    BlogListAssert.assertThat(blogDefinitionDTOS)
        .isSuccessful()
        .hasNumberOfItems(3);
  }

  @Test
  public void should_get_one_blog() {
    Blog blog = givenBlog()
        .buildNumberOfBlogsAndSave(1).get(0);

    BlogDTO blogDTO = thenGetOneBlogFromApiById(blog.getId());
    assertThat(new BlogDTO(blog.getBlogURL(), blog.getDescription(), blog.getName(), blog.getFeedURL(), blog.getPublishedDate(), new ArrayList<>())).isEqualToComparingFieldByField(blogDTO);
  }

  @Test
  public void should_create_a_blog() {
    //given
    blogRepository.deleteAll();
    //when
    thenCreateBlogByApi("test");

    //then
    assertThat(blogRepository.findAll().size()).isEqualTo(1);
    assertThat(blogRepository.findAll().get(0)).isNotNull();
  }

  @Test
  public void should_update_existing_blog() {
    //given
    Instant instant = Instant.now();
    Blog blog = givenBlog()
        .buildNumberOfBlogsAndSave(1).get(0);
    BlogDTO blogDTO = new BlogDTO(blog.getBlogURL(), "desc", blog.getName(), "", instant, new ArrayList<>());

    //when
    thenUpdateBlogByApi(blogDTO);

    //then
    assertThat(blogRepository.findById(blog.getId()).get())
        .isNotNull()
        .hasFieldOrPropertyWithValue("description", "desc")
        .hasFieldOrPropertyWithValue("publishedDate", instant);
  }

  @Test
  public void should_delete_existing_blog() {
    //given
    Blog blog = givenBlog()
        .buildNumberOfBlogsAndSave(1).get(0);
    //when
    thenDeleteOneBlogFromApi(blog.getId());

    //then
    assertThat(blogRepository.findById(blog.getId())).isEmpty();
  }

  @Test
  public void should_get_one_blogWith2Items() {
    Blog blog = givenBlog()
        .buildBlogWithItemsAndSave(2);

    BlogDTO blogDTO = thenGetOneBlogFromApiById(blog.getId());
    assertThat(blogDTO.getItemsList()).isNotEmpty().hasSize(2);
  }

  @Test
  public void should_get_all_blogs_with_items() {
    givenBlog()
        .buildBlogWithItemsAndSave(2);

    List<BlogDTO> dtos = thenGetBlogsFromApi();
    assertThat(dtos).isNotNull().isNotEmpty().hasSize(1);
    assertThat(dtos.get(0).getItemsList()).isNotNull().isNotEmpty().hasSize(2);

  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository);
  }

  private List<BlogDTO> thenGetBlogsFromApi() {
    return Arrays.asList(template.getForEntity(String.format("http://localhost:%s/api/v1/blogs", port), BlogDTO[].class).getBody());
  }

  private BlogDTO thenGetOneBlogFromApiById(Long id) {
    return template.getForEntity(String.format("http://localhost:%s/api/v1/blogs/%s", port, id), BlogDTO.class).getBody();
  }

  private List<BlogDTO> thenGetNumberBlogsFromApi(int number) {
    return Arrays.asList(template.getForEntity(String.format("http://localhost:%s/api/v1/blogs?limit=%s", port, number), BlogDTO[].class).getBody());
  }

  private void thenCreateBlogByApi(String link) {
    template.postForEntity(String.format("http://localhost:%s/api/v1/blogs", port), BlogDTO.builder().link(link).build(), BlogDTO.class);
  }

  private void thenUpdateBlogByApi(BlogDTO blogDTO) {
    template.put(String.format("http://localhost:%s/api/v1/blogs/", port), blogDTO);
  }

  private void thenDeleteOneBlogFromApi(Long blogId) {
    template.delete(String.format("http://localhost:%s/api/v1/blogs/%s", port, blogId));
  }

}
