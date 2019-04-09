package com.gala.sam.tradeEngine.domain.datastructures;

import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue.SortingMethod;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import java.util.Optional;
import java.util.SortedSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class TickerData {

  final String name;
  private final SortedSet<LimitOrder> sellLimitOrders = new LimitOrderQueue(
      SortingMethod.PRICE_ASC);
  private final SortedSet<LimitOrder> buyLimitOrders = new LimitOrderQueue(
      SortingMethod.PRICE_DECS);
  private final SortedSet<MarketOrder> buyMarketOrders = new OrderIdPriorityQueue<>();
  private final SortedSet<MarketOrder> sellMarketOrders = new OrderIdPriorityQueue<>();
  private Optional<Float> lastExecutedTradePrice = Optional.empty();

  public void setLastExecutedTradePrice(float lastExecutedTradePrice) {
    this.lastExecutedTradePrice = Optional.of(lastExecutedTradePrice);
  }

}
