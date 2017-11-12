package pl.michal.olszewski.rssaggregator.config;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrentTimeDateTimeService implements DateTimeService {

  @Override
  public Instant getCurrentDateTime() {
    Instant currentDateAndTime = Instant.now();

    log.info("Returning current date and time: {}", currentDateAndTime);
    return currentDateAndTime;
  }
}
