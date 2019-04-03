package com.gala.sam.tradeEngine.domain.datastructures;

import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue.SortingMethod;
import java.util.Optional;
import java.util.SortedSet;


public class TickerData {

  private final SortedSet<LimitOrder> sellLimitOrders = new LimitOrderQueue(
      SortingMethod.PRICE_ASC);
  private final SortedSet<LimitOrder> buyLimitOrders = new LimitOrderQueue(
      SortingMethod.PRICE_DECS);
  private final SortedSet<MarketOrder> buyMarketOrders = new OrderIdPriorityQueue<>();
  private final SortedSet<MarketOrder> sellMarketOrders = new OrderIdPriorityQueue<>();

  private Optional<Float> lastExecutedTradePrice = Optional.empty();

  public Optional<Float> getLastExecutedTradePrice() {
    return lastExecutedTradePrice;
  }

  public void setLastExecutedTradePrice(float lastExecutedTradePrice) {
    this.lastExecutedTradePrice = Optional.of(lastExecutedTradePrice);
  }

  public SortedSet<LimitOrder> getSellLimitOrders() {
    return sellLimitOrders;
  }

  public SortedSet<LimitOrder> getBuyLimitOrders() {
    return buyLimitOrders;
  }

  public SortedSet<MarketOrder> getBuyMarketOrders() {
    return buyMarketOrders;
  }

  public SortedSet<MarketOrder> getSellMarketOrders() {
    return sellMarketOrders;
  }

  @Override
  public String toString() {
    return "TickerData{" +
        "sellLimitOrders=" + sellLimitOrders +
        ", buyLimitOrders=" + buyLimitOrders +
        ", buyMarketOrders=" + buyMarketOrders +
        ", sellMarketOrders=" + sellMarketOrders +
        ", lastExecutedTradePrice=" + lastExecutedTradePrice +
        '}';
  }
}
