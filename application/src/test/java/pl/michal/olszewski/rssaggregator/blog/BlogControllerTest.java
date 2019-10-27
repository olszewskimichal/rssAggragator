package pl.michal.olszewski.rssaggregator.blog;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;
import org.springframework.web.reactive.function.BodyInserters;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class BlogControllerTest extends IntegrationTestBase {

  @Autowired
  private BlogRepository blogRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private BlogService blogService;

  @BeforeEach
  void setUp() {
    blogRepository.deleteAll();
    mongoTemplate.remove(new Query(), "item");
  }

  @Test
  void should_get_empty_list_of_blogs() {
    //given
    blogService.evictAndRecreateBlogCache();

    //when
    BodySpec<PageBlogDTO, ?> pagedblog = thenGetBlogsFromApi();

    //then
    pagedblog.value(pageBlogDTO -> assertThat(pageBlogDTO.getTotalElements()).isEqualTo(0L));
  }

  @Test
  void should_get_secondPage_of_all_blogs() {
    //given
    givenBlog()
        .buildNumberOfBlogsAndSave(5);
    blogService.evictAndRecreateBlogCache();
    //when
    BodySpec<PageBlogDTO, ?> pagedblog = thenGetBlogsFromApi(2, 3);

    //then
    pagedblog.value(pageBlogDTO -> {
          assertThat(pageBlogDTO.getTotalElements()).isEqualTo(5L);
          assertThat(pageBlogDTO.getContent()).hasSize(2);
        }
    );
  }

  @Test
  void should_get_all_blogs() {
    //given
    givenBlog()
        .buildNumberOfBlogsAndSave(3);
    blogService.evictAndRecreateBlogCache();
    //when
    BodySpec<PageBlogDTO, ?> pagedblog = thenGetBlogsFromApi();

    //then
    pagedblog.value(pageBlogDTO -> assertThat(pageBlogDTO.getTotalElements()).isEqualTo(3L));
  }

  @Test
  void should_get_one_blog() {
    //given
    Blog blog = givenBlog().createAndSaveNewBlog();
    BlogDTO expected =
        new BlogDTOBuilder()
            .id(blog.getId())
            .feedURL(blog.getFeedURL())
            .publishedDate(blog.getPublishedDate())
            .name(blog.getName())
            .description(blog.getDescription())
            .link(blog.getBlogURL())
            .build();

    BodySpec<BlogDTO, ?> blogDTO = thenGetOneBlogFromApiById(blog.getId());

    blogDTO.value(aggregationDTO -> assertThat(aggregationDTO).isEqualToComparingFieldByField(expected));
  }

  @Test
  void should_create_a_blog() {
    //given

    //when
    thenCreateBlogByApi("test");

    //then
    assertThat(blogRepository.count()).isEqualTo(1);
  }

  @Test
  void should_update_existing_blog() {
    //given
    Instant expectedPublishedDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    String expectedDescription = "desc";

    Blog blog = givenBlog().createAndSaveNewBlog();

    BlogDTO blogDTO = new BlogDTOBuilder()
        .link(blog.getBlogURL())
        .description(expectedDescription)
        .name(blog.getName())
        .feedURL(blog.getFeedURL())
        .publishedDate(expectedPublishedDate)
        .build();

    //when
    thenUpdateBlogByApi(blogDTO, blog.getId());

    //then
    assertThat(blogRepository.findById(blog.getId())).isPresent()
        .get()
        .isNotNull()
        .hasFieldOrPropertyWithValue("description", expectedDescription)
        .hasFieldOrPropertyWithValue("publishedDate", expectedPublishedDate);
  }

  @Test
  void should_delete_existing_blog() {
    //given
    Blog blog = givenBlog().createAndSaveNewBlog();

    //when
    thenDeleteOneBlogFromApi(blog.getId());

    //then
    assertThat(blogRepository.findById(blog.getId())).isNotPresent();
  }

  @Test
  void shouldThrowExceptionOnDuplicateKey() {
    //given
    givenBlog()
        .withURL("test");
    //when
    thenThrowExceptionOnCreateBlogByApi("test");
  }

  private BlogListFactory givenBlog() {
    return new BlogListFactory(blogRepository);
  }

  private BodySpec<PageBlogDTO, ?> thenGetBlogsFromApi(int page, int limit) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs?page={page}&limit={limit}", port, page, limit)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageBlogDTO.class);
  }

  private BodySpec<PageBlogDTO, ?> thenGetBlogsFromApi() {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs", port)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageBlogDTO.class);
  }

  private BodySpec<BlogDTO, ?> thenGetOneBlogFromApiById(String id) {
    return webTestClient.get()
        .uri("http://localhost:{port}/api/v1/blogs/{id}", port, id)
        .exchange()
        .expectStatus().isOk()
        .expectBody(BlogDTO.class);
  }

  private void thenCreateBlogByApi(String link) {
    webTestClient.post()
        .uri("http://localhost:{port}/api/v1/blogs", port)
        .body(BodyInserters.fromObject(new BlogDTOBuilder().link(link).build()))
        .exchange()
        .expectStatus().isNoContent();
  }

  private void thenThrowExceptionOnCreateBlogByApi(String link) {
    webTestClient.post()
        .uri("http://localhost:{port}/api/v1/blogs", port)
        .body(BodyInserters.fromObject(new BlogDTOBuilder().link(link).build()))
        .exchange()
        .expectStatus().is5xxServerError();
  }

  private void thenUpdateBlogByApi(BlogDTO blogDTO, String blogId) {
    webTestClient.put()
        .uri("http://localhost:{port}/api/v1/blogs/{blogId}", port, blogId)
        .body(BodyInserters.fromObject(blogDTO))
        .exchange()
        .expectStatus().isNoContent();
  }

  private void thenDeleteOneBlogFromApi(String blogId) {
    webTestClient.delete()
        .uri("http://localhost:{port}/api/v1/blogs/{blogId}", port, blogId)
        .exchange()
        .expectStatus().isNoContent();
  }

}
