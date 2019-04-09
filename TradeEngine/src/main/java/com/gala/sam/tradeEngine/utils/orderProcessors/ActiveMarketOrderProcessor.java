package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorUtils.MarketOrderProcessingContinuer;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveMarketOrderProcessor extends AbstractOrderProcessor<MarketOrder>
implements MarketOrderProcessingContinuer {

  private final OrderProcessorUtils orderProcessorUtils;

  public ActiveMarketOrderProcessor(IOrderRepository orderRepository,
      ITradeRepository tradeRepository, MarketState marketState, MarketUtils marketUtils,
      OrderProcessorUtils orderProcessorUtils) {
    super(orderRepository, tradeRepository, marketState, marketUtils);
    this.orderProcessorUtils = orderProcessorUtils;
  }

  @Override
  public void process(MarketOrder order) {
    log.debug("Order: {} processed as Active Market order", order.getOrderId());
    processMarketOrder(order);
  }

  private void processMarketOrder(MarketOrder marketOrder) {
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
      log.error("Order {} has unsupported direction {} so will not be processed", marketOrder.getOrderId(), marketOrder.getDirection());
    }
  }

  public void processDirectedMarketOrder(MarketOrder marketOrder, TickerData tickerData,
      SortedSet<LimitOrder> limitOrders, SortedSet<MarketOrder> marketOrders) {
    if (limitOrders.isEmpty()) {
      log.debug("Limit order queue empty so no possible limit order matches for market order: {}", marketOrder.getOrderId());
      marketUtils.queueIfGTC(marketOrder, marketOrders, this::saveOrderToDatabase);
    } else {
      LimitOrder limitOrder = limitOrders.first();
      log.debug("Limit order queue not empty, so trading with best limit order: {}",
          limitOrder.toString());
      marketUtils.tryMakeTrade(this::addTradeToStateAndPersist, marketOrder, limitOrder,
          limitOrder.getLimit(), tickerData);
      orderProcessorUtils.removeOrderIfFulfilled(limitOrders, limitOrder,
          this::deleteOrderFromDatabase);
      orderProcessorUtils
          .continueProcessingMarketOrderIfNotFulfilled(marketOrder, tickerData, limitOrders,
              marketOrders, this);
    }
  }
}
