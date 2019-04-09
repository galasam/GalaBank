package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorUtils.LimitOrderProcessingContinuer;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveLimitOrderProcessor extends AbstractOrderProcessor<LimitOrder> implements
    LimitOrderProcessingContinuer {

  private final OrderProcessorUtils orderProcessorUtils;

  public ActiveLimitOrderProcessor(IOrderRepository orderRepository,
      ITradeRepository tradeRepository,
      MarketState marketState, MarketUtils marketUtils,
      OrderProcessorUtils orderProcessorUtils) {
    super(orderRepository, tradeRepository, marketState, marketUtils);
    this.orderProcessorUtils = orderProcessorUtils;
  }

  @Override
  public void process(LimitOrder order) {
    log.debug("Order: {} will be processed as Active Limit order", order.getOrderId());
    processLimitOrder(order);
  }

  private void processLimitOrder(LimitOrder limitOrder) {
    TickerData tickerData = marketState.getTickerQueueGroup(limitOrder);
    if (limitOrder.getDirection() == Direction.BUY) {
      log.debug("Order: {} will be processed as Buy order", limitOrder.getOrderId());
      processDirectedLimitOrder(limitOrder, tickerData,
          tickerData.getSellMarketOrders(),
          tickerData.getBuyLimitOrders(),
          tickerData.getSellLimitOrders());
    } else if (limitOrder.getDirection() == Direction.SELL) {
      log.debug("Order: {} will be processed as Sell order", limitOrder.getOrderId());
      processDirectedLimitOrder(limitOrder, tickerData,
          tickerData.getBuyMarketOrders(),
          tickerData.getSellLimitOrders(),
          tickerData.getBuyLimitOrders());
    } else {
      log.error("Order {} has unsupported direction {} so will not be processed", limitOrder.getOrderId(), limitOrder.getDirection());
    }
  }

  public void processDirectedLimitOrder(LimitOrder limitOrder, TickerData tickerData,
      SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders,
      SortedSet<LimitOrder> oppositeTypeLimitOrders) {
    if (marketOrders.isEmpty()) {
      log.debug("Market order queue empty, so no possible market order matches for limit order: {}", limitOrder.getOrderId());
      if (oppositeTypeLimitOrders.isEmpty()) {
        log.debug("Limit order queue empty, so no possible limit order matches for limit order: {}", limitOrder.getOrderId());
        marketUtils.queueIfGTC(limitOrder, sameTypeLimitOrders, this::saveOrderToDatabase);
      } else {
        LimitOrder otherLimitOrder = oppositeTypeLimitOrders.first();
        log.debug("Limit order queue not empty, so extracted top order: {}",
            otherLimitOrder.toString());
        final boolean limitsMatch;
        try {
          limitsMatch = limitOrder.limitMatches(otherLimitOrder);
        } catch (OrderDirectionNotSupportedException e) {
          log.error("During matching an exception was raised so order {} will not be processed: {}",
              limitOrder.getOrderId(), e.toString());
          return;
        }
        if (limitsMatch) {
          log.debug("Limits match so completing trade with order: {}",
              otherLimitOrder.getOrderId());
          marketUtils.tryMakeTrade(this::addTradeToStateAndPersist, limitOrder, otherLimitOrder,
              otherLimitOrder.getLimit(),
              tickerData);
          orderProcessorUtils.removeOrderIfFulfilled(oppositeTypeLimitOrders, otherLimitOrder,
              this::deleteOrderFromDatabase);
          orderProcessorUtils
              .continueProcessingLimitOrderIfNotFulfilled(limitOrder, tickerData, marketOrders,
                  sameTypeLimitOrders, oppositeTypeLimitOrders, this);
        } else {
          log.debug("Limits do not match, so no trade.");
          marketUtils.queueIfGTC(limitOrder, sameTypeLimitOrders, this::saveOrderToDatabase);
        }
      }
    } else {
      MarketOrder marketOrder = marketOrders.first();
      log.debug("Market order queue not empty, so trading with oldest order: {}", marketOrder
          .toString());
      marketUtils.tryMakeTrade(this::addTradeToStateAndPersist, limitOrder, marketOrder,
          limitOrder.getLimit(),
          tickerData);
      orderProcessorUtils.removeOrderIfFulfilled(marketOrders, marketOrder,
          this::deleteOrderFromDatabase);
      orderProcessorUtils
          .continueProcessingLimitOrderIfNotFulfilled(limitOrder, tickerData, marketOrders,
              sameTypeLimitOrders, oppositeTypeLimitOrders, this);

    }
  }
}
