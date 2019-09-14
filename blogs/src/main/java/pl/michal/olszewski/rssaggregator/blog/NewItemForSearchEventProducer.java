package pl.michal.olszewski.rssaggregator.blog;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;

@Service
class NewItemForSearchEventProducer {

  private final JmsTemplate jmsTemplate;

  NewItemForSearchEventProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  void writeEventToQueue(NewItemForSearchEvent event) {
    jmsTemplate.convertAndSend("searchItems", event);
  }
}
