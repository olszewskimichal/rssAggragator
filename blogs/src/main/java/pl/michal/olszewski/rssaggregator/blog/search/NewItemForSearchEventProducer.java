package pl.michal.olszewski.rssaggregator.blog.search;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;

@Service
public class NewItemForSearchEventProducer {

  private final JmsTemplate jmsTemplate;

  public NewItemForSearchEventProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  public void writeEventToQueue(NewItemForSearchEvent event) {
    jmsTemplate.convertAndSend("searchItems", event);
  }
}
