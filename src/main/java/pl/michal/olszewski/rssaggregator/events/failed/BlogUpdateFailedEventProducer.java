package pl.michal.olszewski.rssaggregator.events.failed;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class BlogUpdateFailedEventProducer {

  private final JmsTemplate jmsTemplate;

  public BlogUpdateFailedEventProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  public void writeEventToQueue(BlogUpdateFailedEvent eventBase) {
    jmsTemplate.convertAndSend("exceptionEvents", eventBase);
  }
}
