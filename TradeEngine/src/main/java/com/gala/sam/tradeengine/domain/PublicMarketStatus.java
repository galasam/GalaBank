package com.gala.sam.tradeengine.domain;

import com.gala.sam.tradeengine.domain.enteredorder.AbstractActiveOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class PublicMarketStatus {

  List<Trade> trades;
  List<Ticker> orders;

  @Getter
  @ToString
  @Builder
  public static class Ticker {

    String name;
    List<AbstractActiveOrder> buy;
    List<AbstractActiveOrder> sell;
  }

}
