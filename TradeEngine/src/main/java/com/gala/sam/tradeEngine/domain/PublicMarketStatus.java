package com.gala.sam.tradeEngine.domain;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractActiveOrder;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PublicMarketStatus {

  List<Trade> trades;
  List<Ticker> orders;

  @Value
  @Builder
  public static class Ticker {

    String name;
    List<AbstractActiveOrder> buy;
    List<AbstractActiveOrder> sell;
  }

}
