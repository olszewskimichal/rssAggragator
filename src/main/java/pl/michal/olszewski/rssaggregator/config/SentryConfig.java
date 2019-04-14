package pl.michal.olszewski.rssaggregator.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {

  @Bean
  public ServletContextInitializer sentryServletContextInitializer() {
    return new io.sentry.spring.SentryServletContextInitializer();
  }
}
