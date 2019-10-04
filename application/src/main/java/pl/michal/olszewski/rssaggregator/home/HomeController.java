package pl.michal.olszewski.rssaggregator.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  private static final Logger log = LoggerFactory.getLogger(HomeController.class);

  @GetMapping("/")
  public String mainPage() {
    log.debug("GET main page");
    return "index";
  }

}
