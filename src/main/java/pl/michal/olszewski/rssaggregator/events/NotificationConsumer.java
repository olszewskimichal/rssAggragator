package pl.michal.olszewski.rssaggregator.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationConsumer {

  @JmsListener(destination = "notif")
  public void receiveMessage(NotificationData object) {
    log.info("Received <" + object + ">");
  }

}
