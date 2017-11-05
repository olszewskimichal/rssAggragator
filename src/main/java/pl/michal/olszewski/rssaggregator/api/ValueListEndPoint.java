package pl.michal.olszewski.rssaggregator.api;

import static java.lang.Math.random;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.michal.olszewski.rssaggregator.entity.ValueList;
import pl.michal.olszewski.rssaggregator.repository.ValueListRepository;
import pl.michal.olszewski.rssaggregator.service.AsyncService;

@RestController
@RequestMapping("/api/v1/values")
@Slf4j
@Transactional
public class ValueListEndPoint {

  @PersistenceContext
  private EntityManager entityManager;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private int batchSize;

  private final ValueListRepository valueListRepository;
  private final AsyncService asyncService;

  public ValueListEndPoint(ValueListRepository valueListRepository, AsyncService asyncService) {
    this.valueListRepository = valueListRepository;
    this.asyncService = asyncService;
  }

  @GetMapping(value = "/1")
  @Transactional(readOnly = true)
  public ResponseEntity<BigDecimal> test1() {
    log.debug("Zaczynam przetwarzanie");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Optional<BigDecimal> max = valueListRepository.findStreamAll()
        .map(v -> v.getValue1().add(v.getValue2()).add(v.getValue3()).add(v.getValue4()).add(v.getValue5()))
        .min(Comparator.naturalOrder());
    stopWatch.stop();
    log.debug("Zakonczono przetwarzanie w czasie {}", stopWatch);
    return max
        .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(value = "/2")
  @Transactional(readOnly = true)
  public ResponseEntity<BigDecimal> test2() {
    log.debug("Zaczynam przetwarzanie");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Optional<BigDecimal> max = valueListRepository.findAll().stream()
        .map(v -> v.getValue1().add(v.getValue2()).add(v.getValue3()).add(v.getValue4()).add(v.getValue5()))
        .max(Comparator.naturalOrder());
    stopWatch.stop();
    log.debug("Zakonczono przetwarzanie w czasie {}", stopWatch);
    return max
        .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(value = "/10")
  @Transactional(readOnly = true)
  public ResponseEntity<BigDecimal> test10() throws ExecutionException, InterruptedException {
    log.debug("Zaczynam przetwarzanie");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    final int pageSize = (int) (valueListRepository.count() / 4);
    int page = 0;
    List<Future<BigDecimal>> futureList = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      futureList.add(asyncService.calculate(page++, pageSize));
    }
    BigDecimal value = BigDecimal.valueOf(Long.MIN_VALUE);
    for (Future<BigDecimal> decimalFuture : futureList) {
      BigDecimal max = decimalFuture.get();
      if (max.compareTo(value) > 0) {
        value = max;
      }
    }
    stopWatch.stop();
    log.debug("Zakonczono przetwarzanie w czasie {}", stopWatch);
    return new ResponseEntity<>(value, HttpStatus.OK);
  }

  @GetMapping(value = "/3")
  @Deprecated  //czas jest ogromny
  public ResponseEntity<BigDecimal> test3() {
    log.debug("Zaczynam przetwarzanie");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    BigDecimal result = BigDecimal.valueOf(Long.MIN_VALUE);
    final int pageLimit = 1000;
    int pageNumber = 0;
    Page<ValueList> valueLists = valueListRepository.findAll(new PageRequest(pageNumber, pageLimit));
    while (valueLists.hasNext()) {
      Optional<BigDecimal> max = valueLists.getContent().stream()
          .map(v -> v.getValue1().add(v.getValue2()).add(v.getValue3()).add(v.getValue4()).add(v.getValue5()))
          .max(Comparator.naturalOrder());
      if (max.isPresent()) {
        BigDecimal maxValue = max.get();
        if (maxValue.compareTo(result) > 0) {
          result = maxValue;
        }
      }
      entityManager.flush();
      entityManager.clear();
      valueLists = valueListRepository.findAll(new PageRequest(++pageNumber, pageLimit));
    }
    Optional<BigDecimal> max = valueLists.getContent().stream()
        .map(v -> v.getValue1().add(v.getValue2()).add(v.getValue3()).add(v.getValue4()).add(v.getValue5()))
        .max(Comparator.naturalOrder());
    if (max.isPresent()) {
      BigDecimal maxValue = max.get();
      if (maxValue.compareTo(result) > 0) {
        result = maxValue;
      }
    }
    stopWatch.stop();
    log.debug("Zakonczono przetwarzanie w czasie {}", stopWatch);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping(value = "/4")
  public void test4() {
    log.debug("Zaczynam wpisywanie do bazy");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    IntStream.rangeClosed(1, 500000).forEach(v -> {
      if (v % 20 == 0) {
        entityManager.flush();
        entityManager.clear();
      }
      valueListRepository
          .save(new ValueList(new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random())));
    });
    entityManager.flush();
    entityManager.clear();
    stopWatch.stop();
    log.debug("Zakonczono wpisywanie do bazy w czasie {}", stopWatch);
  }

  @GetMapping(value = "/5")
  public void test5() {
    log.debug("Zaczynam wpisywanie do bazy");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    List<ValueList> valueListList = IntStream.rangeClosed(1, 500000)
        .mapToObj(v -> new ValueList(new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random())))
        .collect(
            Collectors.toList());
    valueListRepository.save(valueListList);
    entityManager.flush();
    entityManager.clear();
    stopWatch.stop();
    log.debug("Zakonczono wpisywanie do bazy w czasie {}", stopWatch);
  }

  @GetMapping(value = "/6")
  public void test6() {
    log.debug("Zaczynam wpisywanie do bazy");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    IntStream.rangeClosed(1, 500000).forEach(v -> {
      if (v % batchSize == 0) {
        entityManager.flush();
        entityManager.clear();
      }
      entityManager.persist(new ValueList(new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random())));
    });
    entityManager.flush();
    entityManager.clear();
    stopWatch.stop();
    log.debug("Zakonczono wpisywanie do bazy w czasie {}", stopWatch);
  }

  @GetMapping(value = "/7")
  public void test7() throws ExecutionException, InterruptedException {
    log.debug("Zaczynam wpisywanie do bazy");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    List<Future<Boolean>> futureList = new ArrayList<>();
    List<ValueList> valueLists = new ArrayList<>();
    for (int i = 0; i < 500000; i++) {
      valueLists.add(new ValueList(new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random())));
      if (i % 1000 == 0) {
        futureList.add(asyncService.persist(valueLists));
        valueLists = new ArrayList<>();
      }
    }
    futureList.add(asyncService.persist(valueLists));
    for (Future<Boolean> booleanFuture : futureList) {
      booleanFuture.get();
    }
    entityManager.flush();
    entityManager.clear();
    stopWatch.stop();
    log.debug("Zakonczono wpisywanie do bazy w czasie {}", stopWatch);
    log.debug("elementów w baize jest {}", valueListRepository.count());
  }

  @GetMapping(value = "/8")
  public void test8() {
    log.debug("Zaczynam wpisywanie do bazy");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    List<ValueList> valueLists = new ArrayList<>();
    for (int i = 0; i < 500000; i++) {
      valueLists.add(new ValueList(new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random())));
      if (i % 1000 == 0) {
        asyncService.persist(valueLists);
        valueLists = new ArrayList<>();
      }
    }
    asyncService.persist(valueLists);
    entityManager.flush();
    entityManager.clear();
    stopWatch.stop();
    log.debug("Zakonczono wpisywanie do bazy w czasie {}", stopWatch);
    log.debug("elementów w baize jest {}", valueListRepository.count());
  }

  @GetMapping(value = "/9")
  public void test9() throws ExecutionException, InterruptedException {
    log.debug("Zaczynam wpisywanie do bazy");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    List<Future<Boolean>> futureList = new ArrayList<>();
    for (int i = 0; i < 500; i++) {
      futureList.add(asyncService.persist());
    }
    for (Future<Boolean> booleanFuture : futureList) {
      booleanFuture.get();
    }
    entityManager.flush();
    entityManager.clear();
    futureList.clear();
    stopWatch.stop();
    log.debug("Zakonczono wpisywanie do bazy w czasie {}", stopWatch);
    log.debug("elementów w baize jest {}", valueListRepository.count());
  }

  @GetMapping(value = "/count")
  public Long count() {
    log.debug("elementów w baize jest {}", valueListRepository.count());
    return valueListRepository.count();
  }

}
