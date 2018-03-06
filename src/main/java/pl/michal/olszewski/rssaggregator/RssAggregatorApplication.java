package pl.michal.olszewski.rssaggregator;

import java.util.concurrent.Executor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CharacterEncodingFilter;
import pl.michal.olszewski.rssaggregator.config.AuditingDateTimeProvider;
import pl.michal.olszewski.rssaggregator.config.ConstantDateTimeService;
import pl.michal.olszewski.rssaggregator.config.CurrentTimeDateTimeService;
import pl.michal.olszewski.rssaggregator.config.DateTimeService;
import pl.michal.olszewski.rssaggregator.config.Profiles;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EntityScan(
    basePackageClasses = {RssAggregatorApplication.class, Jsr310JpaConverters.class}
)
@EnableScheduling
public class RssAggregatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(RssAggregatorApplication.class, args);
  }

  @Bean(name = "threadPoolTaskExecutor")
  public Executor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(8);
    threadPoolTaskExecutor.setMaxPoolSize(16);
    return threadPoolTaskExecutor;
  }

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    characterEncodingFilter.setForceEncoding(true);
    characterEncodingFilter.setEncoding("UTF-8");
    registrationBean.setFilter(characterEncodingFilter);
    return registrationBean;
  }

  @Profile({Profiles.PRODUCTION, Profiles.DEVELOPMENT})
  @Bean
  DateTimeService currentTimeDateTimeService() {
    return new CurrentTimeDateTimeService();
  }

  @Profile(Profiles.TEST)
  @Bean
  DateTimeService constantDateTimeService() {
    return new ConstantDateTimeService();
  }

  @Bean
  DateTimeProvider dateTimeProvider(DateTimeService dateTimeService) {
    return new AuditingDateTimeProvider(dateTimeService);
  }



}
