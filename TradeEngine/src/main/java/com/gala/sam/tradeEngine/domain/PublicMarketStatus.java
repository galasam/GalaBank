package com.gala.sam.tradeEngine.domain;

import com.gala.sam.tradeEngine.domain.EnteredOrder.ActiveOrder;
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
    List<ActiveOrder> buy;
    List<ActiveOrder> sell;
  }

}
