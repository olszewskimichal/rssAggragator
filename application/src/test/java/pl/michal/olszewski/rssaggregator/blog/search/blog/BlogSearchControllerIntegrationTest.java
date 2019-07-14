package pl.michal.olszewski.rssaggregator.blog.search.blog;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class BlogSearchControllerIntegrationTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "blog");
    TextIndexDefinition textIndex = new TextIndexDefinitionBuilder()
        .onField("name", 2F)
        .onField("description", 1F)
        .build();
    mongoTemplate.indexOps(Blog.class).ensureIndex(textIndex);
  }

  @Test
  void shouldReturnSearchResultByMatchingTextWithoutLimit() {
    mongoTemplate.insertAll(
        List.of(
            Blog.builder().blogURL("link1").name("AAA").build(),
            Blog.builder().blogURL("link2").name("BBB").build(),
            Blog.builder().blogURL("link3").name("CCC").build()
        )
    );

    ListBodySpec<BlogSearchResult> result = thenGetSearchResultFromAPI("AAA", null);

    result.hasSize(1);
  }

  @Test
  void shouldReturnSearchResultByMatchingTextWithLimit() {
    mongoTemplate.insertAll(
        List.of(
            Blog.builder().blogURL("link1").name("BBB").build(),
            Blog.builder().blogURL("link2").name("BBB").build(),
            Blog.builder().blogURL("link3").name("CCC").build()
        )
    );

    ListBodySpec<BlogSearchResult> result = thenGetSearchResultFromAPI("BBB", 1);

    result.hasSize(1);
  }

  private ListBodySpec<BlogSearchResult> thenGetSearchResultFromAPI(String text, Integer limit) {
    return webTestClient.get().uri("http://localhost:{port}/api/v1/blogs/search?text={text}&limit={limit}", port, text, limit)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(BlogSearchResult.class);
  }

}