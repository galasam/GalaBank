package com.gala.sam.tradeEngine.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

  @Id
  int buyOrder;
  int sellOrder;
  String ticker;
  int matchQuantity;
  float matchPrice;

}
