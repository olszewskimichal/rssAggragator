package pl.michal.olszewski.rssaggregator.item;

import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlogItemsServiceTest {

  private BlogItemsService blogService;

  @Mock
  private ItemRepository itemRepository;

  @BeforeEach
  void setUp() {
    blogService = new BlogItemsService(itemRepository);
  }

  @Test
  void shouldReturnBlogItemsForBlog() {
    //given
    Item item = new Item(ItemDTO.builder().title("title").build());

    given(itemRepository.findAllByBlogId("id")).willReturn(Flux.just(item));

    //when
    StepVerifier.create(blogService.getBlogItemsForBlog("id"))
        .expectNextCount(1)
        .verifyComplete();
  }

}
