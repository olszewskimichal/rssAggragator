package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Instant;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.ItemRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class BlogApiTest extends IntegrationTestBase {

  @Autowired
  private BlogReactiveRepository blogRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

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
    BlogDTO expected = new BlogDTO(blog.getBlogURL(), blog.getDescription(), blog.getName(), blog.getFeedURL(), blog.getPublishedDate(), new ArrayList<>()); //TODO skrocic linie

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
        .assertNext(v -> assertThat(v).isNotNull())
        .expectComplete()
        .verify();
  }

  @Test
  void should_update_existing_blog() {
    //given
    Instant instant = Instant.now();
    Blog blog = givenBlog()
        .buildNumberOfBlogsAndSave(1).get(0);
    BlogDTO blogDTO = new BlogDTO(blog.getBlogURL(), "desc", blog.getName(), blog.getFeedURL(), instant, new ArrayList<>()); //TODO skrocic linie

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
    assertThat(blogRepository.findById(blog.getId()).block()).isNull(); //TODO pozbyc sie blocka - zamienic na StepVerifier
  }

  @Test
  void should_get_one_blogWith2Items() {
    //given
    Blog blog = givenBlog()
        .buildBlogWithItemsAndSave(2);
    //when
    BodySpec<BlogDTO, ?> blogDTO = thenGetOneBlogFromApiById(blog.getId());
    //then
    blogDTO.value(v -> assertThat(v).isNotNull());
  }

  @Test
  void should_get_all_blogs_with_items() {
    //given
    givenBlog()
        .buildBlogWithItemsAndSave(2);
    //when
    ListBodySpec<BlogDTO> dtos = thenGetBlogsFromApi();
    //then
    dtos.hasSize(1);
    dtos.value(v -> assertThat(v.get(0).getItemsList()).isNotNull().hasSize(2));
  }

  @Test
  void should_evictCache() { //TODO chyba podobny test jest w BlogCacheTescie - trzeba sprawdzic czy nie lepiej tam to przeniesc
    Flux<Blog> blogs = blogService.getAllBlogs();
    Flux<Blog> blogsNotCached = blogService.getAllBlogs();
    assertSame(blogs, blogsNotCached);
    blogService.evictBlogCache();

    blogs = blogService.getAllBlogs();
    thenEvictCache();
    blogsNotCached = blogService.getAllBlogs(); //TODO
    assertNotSame(blogs, blogsNotCached);  //TODO sprawdzic czy to aby na pewno dziala
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository, mongoTemplate);
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
//TODO pozbyc sie template a uzyc webTestClienta
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
