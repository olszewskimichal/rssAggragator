package pl.michal.olszewski.rssaggregator.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

  @Autowired
  private final JmsTemplate jmsTemplate;

  public NotificationController(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  @GetMapping("/startNotification/{param}")
  public String startNotification(@PathVariable Integer param) {
    for (int i = 0; i < param; i++) {
      NotificationData data = new NotificationData();
      data.setId(i);

      jmsTemplate.convertAndSend("notif", data);

      System.out.println(
          "Notification " + i + ": notification task submitted successfully");
    }
    return "DONE";
  }
}