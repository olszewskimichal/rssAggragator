package pl.michal.olszewski.rssaggregator.blog;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jms.core.JmsTemplate;
import pl.michal.olszewski.rssaggregator.item.ItemDTO;
import pl.michal.olszewski.rssaggregator.item.ItemDTOBuilder;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;
import pl.michal.olszewski.rssaggregator.ogtags.OgTagInfoUpdater;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateBlogWithItemsServiceTest {

  private UpdateBlogWithItemsService blogService;

  @Mock
  private JmsTemplate jmsTemplate;

  @Mock
  private BlogReactiveRepository blogRepository;

  @Mock
  private OgTagInfoUpdater ogTagBlogUpdater;

  @BeforeEach
  void setUp() {
    given(blogRepository.save(any(Blog.class))).willAnswer(i -> {
          Blog argument = i.getArgument(0);
          argument.setId(UUID.randomUUID().toString());
          return Mono.just(argument);
        }
    );
    given(ogTagBlogUpdater.updateItemByOgTagInfo(any(ItemDTO.class))).willAnswer(i -> i.getArgument(0));
    blogService = new UpdateBlogWithItemsService(
        new BlogWorker(blogRepository),
        Caffeine.newBuilder().build(),
        Caffeine.newBuilder().build(),
        new NewItemInBlogEventProducer(jmsTemplate),
        new NewItemForSearchEventProducer(jmsTemplate),
        ogTagBlogUpdater);
  }

  @Test
  void shouldUpdateBlogWhenNewItemAdd() {
    //given
    Blog blog = new BlogBuilder().id(UUID.randomUUID().toString()).feedURL("url").name("url").build();
    given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));

    List<ItemDTO> itemsList = IntStream.rangeClosed(1, 1)
        .mapToObj(number -> new ItemDTOBuilder().date(Instant.now()).author("autor").description("desc").title(number + "").link("link" + number).build()) //TODO
        .collect(Collectors.toList());

    UpdateBlogWithItemsDTO blogDTO = new UpdateBlogWithItemsDTOBuilder()
        .name("url")
        .feedURL("url")
        .itemsList(itemsList)
        .build();

    //when
    Mono<Blog> updateBlog = blogService.updateBlog(blog, blogDTO);

    //then
    StepVerifier.create(updateBlog)
        .expectNextCount(1L)
        .expectComplete()
        .verify();
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(NewItemInBlogEvent.class));
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(NewItemForSearchEvent.class));
  }

  @Test
  void shouldAddItemToBlogOnlyOnce() {
    //given
    ItemDTO itemDTO = new ItemDTOBuilder()
        .title("title")
        .link("url")
        .date(Instant.now())
        .build();

    Blog blog = new BlogBuilder()
        .id(UUID.randomUUID().toString())
        .feedURL("url")
        .name("url")
        .build();

    UpdateBlogWithItemsDTO blogDTO = new UpdateBlogWithItemsDTOBuilder()
        .name("url")
        .feedURL("url")
        .itemsList(Arrays.asList(itemDTO, itemDTO))
        .build();
    given(blogRepository.findByFeedURL("url")).willReturn(Mono.just(blog));

    //when
    Mono<Blog> updateBlog = blogService.updateBlog(blog, blogDTO);

    //then
    StepVerifier.create(updateBlog)
        .expectNextCount(1)
        .expectComplete()
        .verify();
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(NewItemInBlogEvent.class));
    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(NewItemForSearchEvent.class));
  }

}
