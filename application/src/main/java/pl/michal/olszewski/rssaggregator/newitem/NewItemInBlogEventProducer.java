package pl.michal.olszewski.rssaggregator.newitem;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class NewItemInBlogEventProducer {

  private final JmsTemplate jmsTemplate;

  public NewItemInBlogEventProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  public void writeEventToQueue(NewItemInBlogEvent event) {
    jmsTemplate.convertAndSend("newItems", event);
  }
}
