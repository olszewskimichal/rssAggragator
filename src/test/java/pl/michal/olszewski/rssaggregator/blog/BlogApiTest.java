package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Instant;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.ItemRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class BlogApiTest extends IntegrationTestBase {

  @Autowired
  private BlogRepository blogRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private BlogService blogService;

  @BeforeEach
  void setUp() {
    blogRepository.deleteAll().block();
    itemRepository.deleteAll().block();
    blogService.evictBlogCache();
  }

  @Test
  void should_get_empty_list_of_blogs() {
    givenBlog()
        .buildNumberOfBlogsDTOAndSave(0);

    ListBodySpec<BlogDTO> blogs = thenGetBlogsFromApi();

    blogs.hasSize(0);
  }

  @Test
  void should_get_all_blogs() {
    givenBlog()
        .buildNumberOfBlogsDTOAndSave(3);

    ListBodySpec<BlogDTO> blogs = thenGetBlogsFromApi();

    blogs.hasSize(3);
  }

  @Test
  void should_get_limit_three_blogs() {
    givenBlog()
        .buildNumberOfBlogsDTOAndSave(6);

    ListBodySpec<BlogDTO> blogDefinitionDTOS = thenGetBlogsFromApiWithLimit(3);

    blogDefinitionDTOS.hasSize(3);
  }

  @Test
  void should_get_one_blog() {
    Blog blog = givenBlog()
        .buildNumberOfBlogsAndSave(1).get(0);
    BlogDTO expected = new BlogDTO(blog.getBlogURL(), blog.getDescription(), blog.getName(), blog.getFeedURL(), blog.getPublishedDate(), new ArrayList<>());

    BodySpec<BlogDTO, ?> blogDTO = thenGetOneBlogFromApiById(blog.getId());

    blogDTO.value(v -> assertThat(v).isEqualToComparingFieldByField(expected));
  }

  @Test
  void should_create_a_blog() {
    //given
    blogRepository.deleteAll().block();
    //when
    thenCreateBlogByApi("test");

    //then
    StepVerifier.create(blogRepository.findAll())
        .expectNextCount(1)
        .expectComplete()
        .verify();
  }

  @Test
  void should_update_existing_blog() {
    //given
    Instant instant = Instant.now();
    Blog blog = givenBlog()
        .buildNumberOfBlogsAndSave(1).get(0);
    BlogDTO blogDTO = new BlogDTO(blog.getBlogURL(), "desc", blog.getName(), "", instant, new ArrayList<>());

    //when
    thenUpdateBlogByApi(blogDTO);

    //then
    assertThat(blogRepository.findById(blog.getId()).block())
        .isNotNull()
        .hasFieldOrPropertyWithValue("description", "desc")
        .hasFieldOrPropertyWithValue("publishedDate", instant);
  }

  @Test
  void should_delete_existing_blog() {
    //given
    Blog blog = givenBlog()
        .buildNumberOfBlogsAndSave(1).get(0);
    //when
    thenDeleteOneBlogFromApi(blog.getId());

    //then
    StepVerifier.create(blogRepository.findById(blog.getId()))
        .expectNextCount(0)
        .expectComplete()
        .verify();
  }

  @Test
  void should_get_one_blogWith2Items() {
    Blog blog = givenBlog()
        .buildBlogWithItemsAndSave(2);

    BodySpec<BlogDTO, ?> blogDTO = thenGetOneBlogFromApiById(blog.getId());
    blogDTO.value(v -> assertThat(v).isNotNull());
  }

  @Test
  void should_get_all_blogs_with_items() {
    givenBlog()
        .buildBlogWithItemsAndSave(2);

    ListBodySpec<BlogDTO> dtos = thenGetBlogsFromApi();
    dtos.hasSize(1);
    dtos.value(v -> assertThat(v.get(0).getItemsList()).isNotNull().hasSize(2));
  }

  @Test
  void should_evictCache() {
    Flux<Blog> blogs = blogService.getAllBlogs();
    Flux<Blog> blogsNotCached = blogService.getAllBlogs();
    assertSame(blogs, blogsNotCached);
    blogService.evictBlogCache();

    blogs = blogService.getAllBlogs();
    thenEvictCache();
    blogsNotCached = blogService.getAllBlogs();
    assertNotSame(blogs, blogsNotCached);
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository, itemRepository);
  }

  private ListBodySpec<BlogDTO> thenGetBlogsFromApi() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs", port)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(BlogDTO.class);
  }

  private BodySpec<BlogDTO, ?> thenGetOneBlogFromApiById(String id) {
    return webTestClient.get()
        .uri("http://localhost:{port}/api/v1/blogs/{id}", port, id)
        .exchange()
        .expectStatus().isOk()
        .expectBody(BlogDTO.class);
  }

  private ListBodySpec<BlogDTO> thenGetBlogsFromApiWithLimit(int limit) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs?limit={limit}", port, limit)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(BlogDTO.class);
  }

  private void thenCreateBlogByApi(String link) {
    template.postForEntity(String.format("http://localhost:%s/api/v1/blogs", port), BlogDTO.builder().link(link).build(), BlogDTO.class);
  }

  private void thenUpdateBlogByApi(BlogDTO blogDTO) {
    template.put(String.format("http://localhost:%s/api/v1/blogs/", port), blogDTO);
  }

  private void thenDeleteOneBlogFromApi(String blogId) {
    template.delete(String.format("http://localhost:%s/api/v1/blogs/%s", port, blogId));
  }

  private void thenEvictCache() {
    template.postForEntity(String.format("http://localhost:%s/api/v1/blogs/evictCache", port), "", String.class);
  }
}
