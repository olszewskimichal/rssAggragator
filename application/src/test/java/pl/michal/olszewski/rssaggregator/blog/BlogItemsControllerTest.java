package pl.michal.olszewski.rssaggregator.blog;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.ItemListFactory;

class BlogItemsControllerTest extends IntegrationTestBase {

  @Autowired
  private BlogReactiveRepository blogRepository;

  @Autowired
  private MongoTemplate mongoTemplate;


  @Test
  void should_get_all_items_for_blog() {
    //given
    String id = UUID.randomUUID().toString();
    givenItems()
        .buildNumberOfItemsAndSave(2, id);
    //when
    ListBodySpec<ItemDTO> dtos = thenGetBlogsItemsFromApi(id);
    //then
    dtos.hasSize(2);
  }

  private ItemListFactory givenItems() {
    return new ItemListFactory(mongoTemplate);
  }

  private ListBodySpec<ItemDTO> thenGetBlogsItemsFromApi(String id) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/{id}/items", port, id)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ItemDTO.class);
  }
}
