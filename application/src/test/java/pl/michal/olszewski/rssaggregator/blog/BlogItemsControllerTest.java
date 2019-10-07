package pl.michal.olszewski.rssaggregator.blog;

import static java.util.UUID.randomUUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.ItemListFactory;

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
    ListBodySpec<ItemDTO> dtos = thenGetBlogsItemsFromApi(id);
    //then
    dtos.hasSize(2);
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

  private BodyContentSpec thenGet404FromApi(String id) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/{id}/items", port, id)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody();
  }

  private ListBodySpec<ItemDTO> thenGetBlogsItemsFromApi(String id) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/{id}/items", port, id)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }
}
