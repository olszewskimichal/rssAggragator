package pl.michal.olszewski.rssaggregator.home;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

  @GetMapping("/")
  public String mainPage() {
    log.debug("GET main page");
    return "index";
  }

}
