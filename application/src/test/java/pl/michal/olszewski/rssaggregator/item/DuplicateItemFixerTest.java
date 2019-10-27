package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.michal.olszewski.rssaggregator.integration.IntegrationTestBase;

class DuplicateItemFixerTest extends IntegrationTestBase {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private DuplicateItemsFixer duplicateItemsFixer;

  @Autowired
  private MongoTemplate mongoTemplate;

  @BeforeEach
  void setUp() {
    itemRepository.deleteAll();
  }

  @Test
  void shouldRemoveDuplicateLinks() {
    //given
    mongoTemplate.insertAll(
        Arrays.asList(
            new Item(new ItemDTOBuilder()
                .blogId("blogId")
                .link("https://lafkblogs.wordpress.com/2019/09/29/biggest-error-in-your-maven-project-mvn-clean-install/?utm_source=jvm-bloggers.com&utm_medium=link&utm_campaign=jvm-bloggers")
                .build()),
            new Item(new ItemDTOBuilder()
                .blogId("blogId")
                .link("https://lafkblogs.wordpress.com/2019/09/29/biggest-error-in-your-maven-project-mvn-clean-install/")
                .build())
        )
    );

    //when
    duplicateItemsFixer.removeDuplicatesFromLastWeek();

    //then
    assertThat(itemRepository.count()).isEqualTo(1);
    List<String> linkList = itemRepository.findAll().stream().map(Item::getLink).collect(Collectors.toList());
    assertThat(linkList).hasSize(1).contains("https://lafkblogs.wordpress.com/2019/09/29/biggest-error-in-your-maven-project-mvn-clean-install/");
  }

  @Test
  void shouldNotRemoveDuplicateLinksIfThereAreFromDifferentBlog() {
    //given
    mongoTemplate.insertAll(
        Arrays.asList(
            new Item(new ItemDTOBuilder()
                .blogId("blogId")
                .link("https://lafkblogs.wordpress.com/2019/09/29/biggest-error-in-your-maven-project-mvn-clean-install/?utm_source=jvm-bloggers.com&utm_medium=link&utm_campaign=jvm-bloggers")
                .build()),
            new Item(new ItemDTOBuilder()
                .blogId("blogId2")
                .link("https://lafkblogs.wordpress.com/2019/09/29/biggest-error-in-your-maven-project-mvn-clean-install/")
                .build())
        )
    );

    //when
    duplicateItemsFixer.removeDuplicatesFromLastWeek();

    //then
    assertThat(itemRepository.count()).isEqualTo(2);
  }

}
