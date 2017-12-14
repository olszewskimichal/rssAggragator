package pl.michal.olszewski.rssaggregator;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import com.google.common.base.Predicate;
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
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EntityScan(
    basePackageClasses = {RssAggregatorApplication.class, Jsr310JpaConverters.class}
)
@EnableScheduling
@EnableSwagger2
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

  @Bean
  public Docket SwaggerApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("pl.michal.olszewski.rssaggregator.api"))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Rss Aggregator")
        .description("Aggregator blogów ")
        .contact(new Contact("Michał Olszewski", "https://github.com/olszewskimichal", "olszewskimichal@outlook.com"))
        .build();
  }

}
