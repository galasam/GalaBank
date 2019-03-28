package com.gala.sam.tradeEngine.domain.dataStructures;

import com.gala.sam.tradeEngine.domain.EnteredOrder.LimitOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.MarketOrder;
import com.gala.sam.tradeEngine.domain.dataStructures.LimitOrderQueue.SORTING_METHOD;
import java.util.Optional;
import java.util.SortedSet;


public class TickerData {

  private final SortedSet<LimitOrder> sellLimitOrders = new LimitOrderQueue(
      SORTING_METHOD.PRICE_ASC);
  private final SortedSet<LimitOrder> buyLimitOrders = new LimitOrderQueue(
      SORTING_METHOD.PRICE_DECS);
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
