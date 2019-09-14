package pl.michal.olszewski.rssaggregator.item;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import pl.michal.olszewski.rssaggregator.search.NewItemForSearchEvent;

@Service
class MigrateItemForSearchEventProducer {

  private final JmsTemplate jmsTemplate;

  MigrateItemForSearchEventProducer(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  void writeEventToQueue(NewItemForSearchEvent event) {
    jmsTemplate.convertAndSend("searchItems", event);
  }
}
