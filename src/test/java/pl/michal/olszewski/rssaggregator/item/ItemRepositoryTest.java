package pl.michal.olszewski.rssaggregator.item;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mongodb.MongoWriteException;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import pl.michal.olszewski.rssaggregator.blog.Blog;
import pl.michal.olszewski.rssaggregator.blog.BlogRepository;
import pl.michal.olszewski.rssaggregator.config.Profiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles(Profiles.TEST)
public class ItemRepositoryTest {

    @Autowired
    protected MongoTemplate entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BlogRepository blogRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll().block();
        blogRepository.deleteAll();
    }

    @Test
    void shouldFind2NewestItems() {
        //given
        Blog blog = new Blog("url", "", "", "", null, null);
        Instant instant = Instant.now();
        entityManager.save(blog);
        Item title1 = new Item(ItemDTO.builder().link("title1").date(instant).build());
        Item title3 = new Item(ItemDTO.builder().link("title3").date(instant.plusSeconds(10)).build());
        blog.addItem(title1, entityManager);
        blog.addItem(new Item(ItemDTO.builder().link("title2").date(instant.minusSeconds(10)).build()), entityManager);
        blog.addItem(title3, entityManager);
        entityManager.save(blog);

        //when
        Flux<Item> items = itemRepository.findAllBy(PageRequest.of(0, 2, new Sort(Direction.DESC, "date")));

        //then
        StepVerifier.create(items)
            .expectNextCount(2)
            .expectComplete()
            .verify();
        StepVerifier.create(items)
            .expectNext(title3)
            .expectNext(title1)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldFindItemsWhenDateIsNull() {
        //given
        Blog blog = new Blog("url", "", "", "", null, null);
        blog.addItem(new Item(ItemDTO.builder().link("title1").date(Instant.now()).build()), entityManager);
        blog.addItem(new Item(ItemDTO.builder().link("title2").date(Instant.now()).build()), entityManager);
        blog.addItem(new Item(ItemDTO.builder().link("title3").date(Instant.now()).build()), entityManager);
        entityManager.save(blog);

        //when
        Flux<Item> items = itemRepository.findAllBy(PageRequest.of(0, 2, new Sort(Direction.DESC, "date")));

        //then
        StepVerifier.create(items)
            .expectNextCount(2)
            .expectComplete()
            .verify();
    }

    @Test
    void shouldNotCreateItemByUniqueConstraint() {
        Blog blog = new Blog("url", "", "", "", null, null);
        blog.addItem(new Item(ItemDTO.builder().link("title1").build()), entityManager);
        entityManager.save(blog);
        assertThatThrownBy(() -> blog.addItem(new Item(ItemDTO.builder().link("title1").description("desc").build()), entityManager))
            .hasMessageContaining("duplicate key error collection")
            .hasCauseInstanceOf(MongoWriteException.class);
    }


}
