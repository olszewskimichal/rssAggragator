package pl.michal.olszewski.rssaggregator.units;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.michal.olszewski.rssaggregator.entity.Blog;
import pl.michal.olszewski.rssaggregator.exception.BlogNotFoundException;
import pl.michal.olszewski.rssaggregator.service.AsyncService;
import pl.michal.olszewski.rssaggregator.service.BlogService;
import pl.michal.olszewski.rssaggregator.service.UpdateBlogService;

public class UpdateBlogServiceTest {

  private UpdateBlogService updateBlogService;

  @Mock
  private BlogService blogService;
  @Mock
  private AsyncService asyncService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    updateBlogService = new UpdateBlogService(blogService, asyncService);
  }

  @Test
  public void shouldNotUpdateBlogFromId() {
    //given
    given(blogService.getBlogById(1L)).willThrow(new BlogNotFoundException(1L));
    //when
    //then
    assertThatThrownBy(() -> updateBlogService.updateBlogFromId(1L)).hasMessageContaining("Nie znaleziono bloga");
  }

  @Test
  public void shouldUpdateBlogFromId() {
    //given
    given(blogService.getBlogById(1L)).willReturn(new Blog());
    //when
    updateBlogService.updateBlogFromId(1L);

    verify(blogService, times(1)).getBlogById(1L);
    verify(asyncService, times(1)).updateBlog(new Blog());
    verifyNoMoreInteractions(blogService);
  }
}