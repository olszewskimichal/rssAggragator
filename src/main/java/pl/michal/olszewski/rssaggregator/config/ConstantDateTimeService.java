package pl.michal.olszewski.rssaggregator.config;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConstantDateTimeService implements DateTimeService {

  @Override
  public Instant getCurrentDateTime() {
    Instant instant = Instant.parse("2000-01-01T10:00:55.000Z");
    log.info("Returning current date and time: {}", instant);
    return instant;
  }
}
