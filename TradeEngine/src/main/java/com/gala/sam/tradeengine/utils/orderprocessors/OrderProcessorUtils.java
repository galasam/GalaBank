package com.gala.sam.tradeengine.utils.orderprocessors;

import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.domain.datastructures.TickerData;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeengine.domain.enteredorder.MarketOrder;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderProcessorUtils {

  public void continueProcessingLimitOrderIfNotFulfilled(List<Trade> trades, LimitOrder order,
      TickerData tickerData, SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders, SortedSet<LimitOrder> oppositeTypeLimitOrders,
      LimitOrderProcessingContinuer limitOrderProcessingContinuer) {
    if (!order.isFullyFulfilled()) {
      log.debug("New limit order {} is not fully satisfied, so continue processing it.",
          order.getOrderId());
      limitOrderProcessingContinuer
          .processDirectedLimitOrder(trades, order, tickerData, marketOrders,
              sameTypeLimitOrders, oppositeTypeLimitOrders);
    } else {
      log.debug("New limit order {} is fully satisfied, so drop it.", order.getOrderId());
    }
  }

  public void continueProcessingMarketOrderIfNotFulfilled(MarketOrder order,
      MarketOrderProcessingContinuer marketOrderProcessingContinuer) {
    if (!order.isFullyFulfilled()) {
      log.debug("New limit order {} is not fully satisfied, so continue processing it.",
          order.getOrderId());
      marketOrderProcessingContinuer.start();
    } else {
      log.debug("New limit order {} is fully satisfied, so drop it.", order.getOrderId());
    }
  }

  public <T extends AbstractOrder> void removeOrderIfFulfilled(SortedSet<T> orders, T order,
      Consumer<AbstractOrder> deleteOrderFromDatabase) {
    if (order.isFullyFulfilled()) {
      log.debug("Order {} is fully satisfied so remove from queue", order.getOrderId());
      orders.remove(order);
      deleteOrderFromDatabase.accept(order);
    }
  }

  public interface LimitOrderProcessingContinuer {

    void processDirectedLimitOrder(List<Trade> trades, LimitOrder limitOrder, TickerData tickerData,
        SortedSet<MarketOrder> marketOrders,
        SortedSet<LimitOrder> sameTypeLimitOrders,
        SortedSet<LimitOrder> oppositeTypeLimitOrders);
  }

  public interface MarketOrderProcessingContinuer {

    void start();
  }
}
