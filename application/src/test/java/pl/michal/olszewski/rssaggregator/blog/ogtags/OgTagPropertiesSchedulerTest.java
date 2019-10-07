package pl.michal.olszewski.rssaggregator.blog.ogtags;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogBuilder;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class OgTagPropertiesSchedulerTest extends IntegrationTestBase {

  @Autowired
  private MongoTemplate mongoTemplate;
  @Autowired
  private OgTagPropertiesScheduler propertiesScheduler;

  @BeforeEach
  void setUp() {
    mongoTemplate.remove(new Query(), "blog");
  }

  @Test
  void shouldUpdateAllBlogsFromScheduler() {
    //given
    Blog blog1 = new BlogBuilder().blogURL("http://devstyle.pl").build();
    Blog blog2 = new BlogBuilder().blogURL("http://54.38.53.100/").build();
    mongoTemplate.insertAll(List.of(blog1, blog2));
    //when
    propertiesScheduler.updateBlogPropertiesFromOgTagsInfo();

    //then
    Blog updated = mongoTemplate.findOne(new Query().addCriteria(Criteria.where("id").is(blog1.getId())), Blog.class);
    assertThat(updated.getImageUrl()).isNotNull();
    Blog notUpdated = mongoTemplate.findOne(new Query().addCriteria(Criteria.where("id").is(blog2.getId())), Blog.class);
    assertThat(notUpdated.getImageUrl()).isNotNull();
  }

}
