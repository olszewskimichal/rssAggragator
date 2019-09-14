package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.item.NewItemInBlogEvent;

@Service
class NewItemInBlogEventProducer {

  private final JmsTemplate jmsTemplate;

  public NewItemInBlogEventProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  void writeEventToQueue(NewItemInBlogEvent event) {
    jmsTemplate.convertAndSend("newItems", event);
  }
}
