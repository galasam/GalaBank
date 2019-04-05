package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderTimeInForceNotSupportedException;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveMarketOrderProcessor extends AbstractOrderProcessor {

  public ActiveMarketOrderProcessor(IOrderRepository orderRepository,
      ITradeRepository tradeRepository, MarketState marketState, MarketUtils marketUtils) {
    super(orderRepository, tradeRepository, marketState, marketUtils);
  }

  @Override
  public <T extends AbstractOrder> void process(T order)
      throws OrderDirectionNotSupportedException, OrderTimeInForceNotSupportedException {
    log.debug("Order: {} processed as Active Market order", order.getOrderId());
    processMarketOrder((MarketOrder) order);
  }

  private void processMarketOrder(MarketOrder marketOrder)
      throws OrderDirectionNotSupportedException, OrderTimeInForceNotSupportedException {
    TickerData tickerData = marketState.getTickerQueueGroup(marketOrder);
    if (marketOrder.getDirection() == Direction.BUY) {
      log.debug("Order: {} processed as Buy order", marketOrder.getOrderId());
      processDirectedMarketOrder(marketOrder, tickerData,
          tickerData.getSellLimitOrders(), tickerData.getBuyMarketOrders());
    } else if (marketOrder.getDirection() == Direction.SELL) {
      log.debug("Order: {} processed as Sell order", marketOrder.getOrderId());
      processDirectedMarketOrder(marketOrder, tickerData,
          tickerData.getBuyLimitOrders(), tickerData.getSellMarketOrders());
    } else {
      throw new OrderDirectionNotSupportedException(marketOrder.getDirection());
    }
  }

  private void processDirectedMarketOrder(MarketOrder marketOrder, TickerData tickerData,
      SortedSet<LimitOrder> limitOrders, SortedSet<MarketOrder> marketOrders)
      throws OrderTimeInForceNotSupportedException, OrderDirectionNotSupportedException {
    if (limitOrders.isEmpty()) {
      log.debug("Limit order queue empty so no possible limit order matches for market order: {}", marketOrder.getOrderId());
      marketUtils.queueIfTimeInForce(marketOrder, marketOrders, this::saveOrderToDatabase);
    } else {
      LimitOrder limitOrder = limitOrders.first();
      log.debug("Limit order queue not empty, so trading with best limit order: {}",
          limitOrder.toString());
      marketUtils.makeTrade(this::addTradeToStateAndPersist, marketOrder, limitOrder, limitOrder.getLimit(), tickerData);
      if (limitOrder.isFullyFulfilled()) {
        log.debug("Limit order {} is fully satisfied so removing", limitOrder.getOrderId());
        limitOrders.remove(limitOrder);
        deleteOrderFromDatabase(limitOrder);
      }
      if (!marketOrder.isFullyFulfilled()) {
        log.debug("New market order {} is not fully satisfied so continue processing .",
            marketOrder);
        processDirectedMarketOrder(marketOrder, tickerData, limitOrders, marketOrders);
      }
    }
  }

}
