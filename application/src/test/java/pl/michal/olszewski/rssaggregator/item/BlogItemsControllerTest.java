package pl.michal.olszewski.rssaggregator.item;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;
import pl.michal.olszewski.rssaggregator.blog.BlogBuilder;
import pl.michal.olszewski.rssaggregator.blog.BlogSyncRepository;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class BlogItemsControllerTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private BlogSyncRepository blogRepository;

  @BeforeEach
  void setUp() {
    blogRepository.deleteAll();
  }

  @Test
  void should_get_all_items_for_blog() {
    //given
    String id = randomUUID().toString();
    blogRepository.save(new BlogBuilder().id(id).build());
    givenItems()
        .buildNumberOfItemsAndSave(2, id);
    //when
    BodySpec<PageBlogItemDTO, ?> result = thenGetBlogsItemsFromApi(id);
    //then
    result.value(
        pageBlogItemDTO -> assertThat(pageBlogItemDTO.getTotalElements()).isEqualTo(2L)
    );
  }

  @Test
  void should_get_2page_of_all_items_for_blog() {
    //given
    String id = randomUUID().toString();
    blogRepository.save(new BlogBuilder().id(id).build());
    givenItems()
        .buildNumberOfItemsAndSave(5, id);
    //when
    BodySpec<PageBlogItemDTO, ?> result = thenGetBlogsItemsFromApi(id, 2, 3);
    //then
    result.value(
        pageBlogItemDTO -> {
          assertThat(pageBlogItemDTO.getTotalElements()).isEqualTo(5L);
          assertThat(pageBlogItemDTO.getContent()).hasSize(2);
        }
    );
  }


  @Test
  void should_return_404_for_blog_that_not_exists() {
    //given
    String id = randomUUID().toString();
    //when
    thenGet404FromApi(id);
  }

  private ItemListFactory givenItems() {
    return new ItemListFactory(mongoTemplate);
  }

  private void thenGet404FromApi(String id) {
    webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/{id}/items", port, id)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody();
  }

  private BodySpec<PageBlogItemDTO, ?> thenGetBlogsItemsFromApi(String id, int page, int limit) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/{id}/items?page={page}&limit={limit}", port, id, page, limit)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageBlogItemDTO.class);
  }

  private BodySpec<PageBlogItemDTO, ?> thenGetBlogsItemsFromApi(String id) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/{id}/items", port, id)
        .exchange()
        .expectStatus().isOk()
        .expectBody(PageBlogItemDTO.class);
  }
}
