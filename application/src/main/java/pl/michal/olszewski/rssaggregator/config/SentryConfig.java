package pl.michal.olszewski.rssaggregator.config;

import io.sentry.Sentry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SentryConfig {

  @Bean
  @Profile("prod")
  public HandlerExceptionResolver sentryExceptionResolver() {
    Sentry.init("https://9ac999c61667441989caec4a57df4c8c@sentry.io/1438746?option");
    return new io.sentry.spring.SentryExceptionResolver();
  }
}
