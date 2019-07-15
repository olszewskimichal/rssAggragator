package pl.michal.olszewski.rssaggregator.blog.search.items;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import pl.michal.olszewski.rssaggregator.item.Item;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import reactor.test.StepVerifier;

class ItemTextSearchRepositoryImplTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ItemTextSearchRepository itemTextSearchRepository;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "item");
  }

  @Test
  void shouldFindItemWhereTitleMatchingToQuery() {
    //given
    List<Item> blogList = Arrays.asList(
        new Item(ItemDTO.builder().link("URL1").title("TDD in JAVA").description("TDD").build()),
        new Item(ItemDTO.builder().link("URL2").title("TDD in PYTHON").description("TDD").build()),
        new Item(ItemDTO.builder().link("URL3").title("TDD in JAVASCRIPT").description("TDD").build())
    );
    mongoTemplate.insertAll(blogList);
    //when
    StepVerifier.create(itemTextSearchRepository.findMatching("Java", 1))
        .assertNext(searchResult -> assertThat(searchResult.getTitle()).isEqualTo("TDD in JAVA"))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldFindItemWhereDescriptionMatchingToQuery() {
    //given
    List<Item> blogList = Arrays.asList(
        new Item(ItemDTO.builder().link("URL1").description("TDD in JAVA").title("TDD").build()),
        new Item(ItemDTO.builder().link("URL2").description("TDD in PYTHON").title("TDD").build()),
        new Item(ItemDTO.builder().link("URL3").description("TDD in JAVASCRIPT").title("TDD").build())
    );
    mongoTemplate.insertAll(blogList);
    //when
    StepVerifier.create(itemTextSearchRepository.findMatching("Java", 1))
        .assertNext(searchResult -> assertThat(searchResult.getDescription()).isEqualTo("TDD in JAVA"))
        .expectComplete()
        .verify();
  }
}