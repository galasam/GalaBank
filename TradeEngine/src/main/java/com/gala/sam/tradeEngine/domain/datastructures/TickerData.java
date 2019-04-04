package com.gala.sam.tradeEngine.domain.datastructures;

import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue.SortingMethod;
import java.util.Optional;
import java.util.SortedSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class TickerData {

  private final SortedSet<LimitOrder> sellLimitOrders = new LimitOrderQueue(
      SortingMethod.PRICE_ASC);
  private final SortedSet<LimitOrder> buyLimitOrders = new LimitOrderQueue(
      SortingMethod.PRICE_DECS);
  private final SortedSet<MarketOrder> buyMarketOrders = new OrderIdPriorityQueue<>();
  private final SortedSet<MarketOrder> sellMarketOrders = new OrderIdPriorityQueue<>();

  final String name;

  private Optional<Float> lastExecutedTradePrice = Optional.empty();

  public void setLastExecutedTradePrice(float lastExecutedTradePrice) {
    this.lastExecutedTradePrice = Optional.of(lastExecutedTradePrice);
  }

}
