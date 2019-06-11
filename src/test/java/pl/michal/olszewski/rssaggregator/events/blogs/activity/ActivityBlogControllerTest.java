package pl.michal.olszewski.rssaggregator.events.blogs.activity;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jms.core.JmsTemplate;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActivityBlogControllerTest {

  private ActivityBlogController activityBlogController;
  @Mock
  private JmsTemplate jmsTemplate;

  @BeforeEach
  void setUp() {
    activityBlogController = new ActivityBlogController(new BlogActivityEventProducer(jmsTemplate));
  }

  @Test
  void shouldAddNewActivateBlogEventToQueue() {
    StepVerifier.create(
        activityBlogController.enableBlogById("id"))
        .expectNext(true)
        .expectComplete()
        .verify();

    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(ActivateBlog.class));
  }

  @Test
  void shouldAddNewDeactivateBlogEventToQueue() {
    StepVerifier.create(
        activityBlogController.disableBlogById("id"))
        .expectNext(true)
        .expectComplete()
        .verify();

    verify(jmsTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any(DeactivateBlog.class));
  }

}