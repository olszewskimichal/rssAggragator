package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class BlogActivityEventProducer {

  private final JmsTemplate jmsTemplate;

  public BlogActivityEventProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  void writeEventToQueue(ActivateBlog event) {
    jmsTemplate.convertAndSend("activateBlogEvent", event);
  }

  void writeEventToQueue(DeactivateBlog event) {
    jmsTemplate.convertAndSend("deactivateBlogEvent", event);
  }
}
