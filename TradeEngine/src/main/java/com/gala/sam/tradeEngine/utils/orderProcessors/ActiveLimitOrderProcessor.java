package com.gala.sam.tradeEngine.utils.orderProcessors;

import static com.gala.sam.tradeEngine.utils.MarketUtils.tryMakeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfGTC;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.exception.AbstractOrderFieldNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import java.io.IOException;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveLimitOrderProcessor extends AbstractOrderProcessor<LimitOrder> {

  final MarketState marketState;

  public ActiveLimitOrderProcessor(IOrderRepository orderRepository, ITradeRepository tradeRepository,
      MarketState marketState) {
    super(orderRepository, tradeRepository);
    this.marketState = marketState;
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

  private void processDirectedLimitOrder(LimitOrder limitOrder, TickerData tickerData,
      SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders,
      SortedSet<LimitOrder> oppositeTypeLimitOrders) {
    if (marketOrders.isEmpty()) {
      log.debug("Market order queue empty, so no possible market order matches for limit order: {}", limitOrder.getOrderId());
      if (oppositeTypeLimitOrders.isEmpty()) {
        log.debug("Limit order queue empty, so no possible limit order matches for limit order: {}", limitOrder.getOrderId());
        queueIfGTC(limitOrder, sameTypeLimitOrders, this::saveOrder);
      } else {
        LimitOrder otherLimitOrder = oppositeTypeLimitOrders.first();
        log.debug("Limit order queue not empty, so extracted top order: {}", otherLimitOrder.toString());
        final boolean limitsMatch;
        try {
          limitsMatch = limitOrder.limitMatches(otherLimitOrder);
        } catch (OrderDirectionNotSupportedException e) {
          log.error("During matching an exception was raised so order {} will not be processed: {}", limitOrder.getOrderId(), e.toString());
          return;
        }
        if (limitsMatch) {
          log.debug("Limits match so completing trade with order: {}", otherLimitOrder.getOrderId());
            tryMakeTrade(marketState, limitOrder, otherLimitOrder, otherLimitOrder.getLimit(),
                tickerData, this::saveTrade);
          removeOrderIfFulfilled(oppositeTypeLimitOrders, otherLimitOrder);
          continueProcessingLimitOrderIfNotFulfilled(limitOrder, tickerData, marketOrders,
              sameTypeLimitOrders, oppositeTypeLimitOrders);
        } else {
          log.debug("Limits do not match, so no trade.");
          queueIfGTC(limitOrder, sameTypeLimitOrders, this::saveOrder);
        }
      }
    } else {
      MarketOrder marketOrder = marketOrders.first();
      log.debug("Market order queue not empty, so trading with oldest order: {}", marketOrder
          .toString());
      tryMakeTrade(marketState, marketOrder, limitOrder, limitOrder.getLimit(), tickerData,
          this::saveTrade);
      removeOrderIfFulfilled(marketOrders, marketOrder);
      continueProcessingLimitOrderIfNotFulfilled(limitOrder, tickerData, marketOrders,
          sameTypeLimitOrders, oppositeTypeLimitOrders);

    }
  }

  private void continueProcessingLimitOrderIfNotFulfilled(LimitOrder limitOrder,
      TickerData tickerData, SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders, SortedSet<LimitOrder> oppositeTypeLimitOrders) {
    if (!limitOrder.isFullyFulfilled()) {
      log.debug("New limit order {} is not fully satisfied, so continue processing it.", limitOrder.getOrderId());
      processDirectedLimitOrder(limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
          oppositeTypeLimitOrders);
    } else {
      log.debug("New limit order {} is fully satisfied, so drop it.", limitOrder.getOrderId());
    }
  }

  private <T extends AbstractOrder> void removeOrderIfFulfilled(SortedSet<T> orders, T order) {
    if (order.isFullyFulfilled()) {
      log.debug("Order {} is fully satisfied so remove from queue", order.getOrderId());
      orders.remove(order);
      deleteOrder(order);
    }
  }

}
