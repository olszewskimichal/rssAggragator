package pl.michal.olszewski.rssaggregator.blog.search;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;
import reactor.test.StepVerifier;

class BlogTextSearchRepositoryImplTest extends IntegrationTestBase {


  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private BlogTextSearchRepository blogTextSearchRepository;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "blog");
  }

  @Test
  void shouldFindBlogWhereTitleMatchingToQuery() {
    //given
    List<Blog> blogList = Arrays.asList(
        Blog.builder().blogURL("URL1").name("TDD in JAVA").description("TDD").build(),
        Blog.builder().blogURL("URL2").name("TDD in PYTHON").description("TDD").build(),
        Blog.builder().blogURL("URL3").name("TDD in JAVASCRIPT").description("TDD").build()
    );
    mongoTemplate.insertAll(blogList);
    //when
    StepVerifier.create(blogTextSearchRepository.findMatching("Java", 1))
        .assertNext(blog -> assertThat(blog.getName()).isEqualTo("TDD in JAVA"))
        .expectComplete()
        .verify();
  }

  @Test
  void shouldFindBlogWhereDescriptionMatchingToQuery() {
    //given
    List<Blog> blogList = Arrays.asList(
        Blog.builder().blogURL("URL1").description("TDD in JAVA").name("TDD").build(),
        Blog.builder().blogURL("URL2").description("TDD in PYTHON").name("TDD").build(),
        Blog.builder().blogURL("URL3").description("TDD in JAVASCRIPT").name("TDD").build()
    );
    mongoTemplate.insertAll(blogList);
    //when
    StepVerifier.create(blogTextSearchRepository.findMatching("Java", 1))
        .assertNext(blog -> assertThat(blog.getDescription()).isEqualTo("TDD in JAVA"))
        .expectComplete()
        .verify();
  }
}