package pl.michal.olszewski.rssaggregator.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile({Profiles.DEVELOPMENT, Profiles.PRODUCTION})
public class InitDb {
  
  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }
}
