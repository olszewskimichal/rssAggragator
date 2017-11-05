package pl.michal.olszewski.rssaggregator.service;

import static java.lang.Math.random;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.michal.olszewski.rssaggregator.entity.ValueList;
import pl.michal.olszewski.rssaggregator.repository.ValueListRepository;

@Service
@Transactional(readOnly = true)
@Slf4j
public class AsyncService {

  private final ValueListRepository valueListRepository;

  @PersistenceContext
  private EntityManager entityManager;

  public AsyncService(ValueListRepository valueListRepository) {
    this.valueListRepository = valueListRepository;
  }

  @Async("threadPoolTaskExecutor")
  public Future<Boolean> persist(ValueList valueList) {
    valueListRepository.save(valueList);
    entityManager.flush();
    entityManager.clear();
    return new AsyncResult<>(true);
  }

  @Async("threadPoolTaskExecutor")
  public Future<Boolean> persist(List<ValueList> valueLists) {
    IntStream.rangeClosed(1, valueLists.size()).forEach(v -> {
      if (v % 20 == 0) {
        entityManager.flush();
        entityManager.clear();
      }
      entityManager.persist(valueLists.get(v - 1));
    });
    entityManager.flush();
    entityManager.clear();
    valueLists.clear();
    return new AsyncResult<>(true);
  }


  @Async("threadPoolTaskExecutor")
  public Future<Boolean> persist() {
    IntStream.rangeClosed(1, 1000).forEach(v -> {
      if (v % 20 == 0) {
        entityManager.flush();
        entityManager.clear();
      }
      entityManager.persist(new ValueList(new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random()), new BigDecimal(random())));
    });
    entityManager.flush();
    entityManager.clear();
    return new AsyncResult<>(true);
  }

  @Async("threadPoolTaskExecutor")
  public Future<BigDecimal> calculate(long page, long pageSize) {
    log.debug("page {} page size {}", page, pageSize);
    Optional<BigDecimal> max = valueListRepository.findStreamAll(page * pageSize, (page + 1) * pageSize)
        .map(v -> v.getValue1().add(v.getValue2()).add(v.getValue3()).add(v.getValue4()).add(v.getValue5()))
        .min(Comparator.naturalOrder());
    log.debug(max.toString());
    return new AsyncResult<>(max.orElse(BigDecimal.ZERO));
  }
}
