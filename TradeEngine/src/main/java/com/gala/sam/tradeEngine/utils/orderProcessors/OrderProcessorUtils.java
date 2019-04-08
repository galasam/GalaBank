package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.utils.exception.ProcessingActiveOrderException;
import java.util.SortedSet;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderProcessorUtils {

  public interface LimitOrderProcessingContinuer {

    void processDirectedLimitOrder(LimitOrder limitOrder, TickerData tickerData,
        SortedSet<MarketOrder> marketOrders,
        SortedSet<LimitOrder> sameTypeLimitOrders,
        SortedSet<LimitOrder> oppositeTypeLimitOrders) throws ProcessingActiveOrderException;
  }

  public void continueProcessingLimitOrderIfNotFulfilled(LimitOrder limitOrder,
      TickerData tickerData, SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders, SortedSet<LimitOrder> oppositeTypeLimitOrders,
      LimitOrderProcessingContinuer limitOrderProcessingContinuer)
      throws ProcessingActiveOrderException {
    if (!limitOrder.isFullyFulfilled()) {
      log.debug("New limit order {} is not fully satisfied, so continue processing it.",
          limitOrder.getOrderId());
      limitOrderProcessingContinuer
          .processDirectedLimitOrder(limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
              oppositeTypeLimitOrders);
    } else {
      log.debug("New limit order {} is fully satisfied, so drop it.", limitOrder.getOrderId());
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
}
