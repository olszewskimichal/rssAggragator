package pl.michal.olszewski.rssaggregator.entity;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Entity
@Data
@NoArgsConstructor
public class ValueList {

  @Id
  @GeneratedValue
  private Long id;

  private BigDecimal value1;

  private BigDecimal value2;

  private BigDecimal value3;

  private BigDecimal value4;

  private BigDecimal value5;

  public ValueList(BigDecimal value1, BigDecimal value2, BigDecimal value3, BigDecimal value4, BigDecimal value5) {
    this.value1 = value1;
    this.value2 = value2;
    this.value3 = value3;
    this.value4 = value4;
    this.value5 = value5;
  }
}
