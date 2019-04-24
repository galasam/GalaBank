package com.gala.sam.tradeengine.utils.orderprocessors;

import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.domain.datastructures.MarketState;
import com.gala.sam.tradeengine.domain.datastructures.TickerData;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeengine.domain.enteredorder.MarketOrder;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.utils.MarketUtils;
import com.gala.sam.tradeengine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeengine.utils.orderprocessors.OrderProcessorUtils.LimitOrderProcessingContinuer;
import java.util.List;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActiveLimitOrderProcessor extends AbstractOrderProcessor<LimitOrder> implements
    LimitOrderProcessingContinuer {

  private final OrderProcessorUtils orderProcessorUtils;

  public ActiveLimitOrderProcessor(MarketUtils marketUtils,
      IOrderRepository orderRepository,
      ITradeRepository tradeRepository,
      OrderProcessorUtils orderProcessorUtils) {
    super(marketUtils, orderRepository, tradeRepository);
    this.orderProcessorUtils = orderProcessorUtils;
  }

  @Override
  public void process(MarketState marketState, LimitOrder order) {
    log.debug("Order: {} will be processed as Active Limit order", order.getOrderId());
    processLimitOrder(marketState, order);
  }

  private void processLimitOrder(MarketState marketState, LimitOrder limitOrder) {
    TickerData tickerData = marketState.getTickerQueueGroup(limitOrder);
    if (limitOrder.getDirection() == Direction.BUY) {
      log.debug("Order: {} will be processed as Buy order", limitOrder.getOrderId());
      processDirectedLimitOrder(marketState.getTrades(), limitOrder, tickerData,
          tickerData.getSellMarketOrders(),
          tickerData.getBuyLimitOrders(),
          tickerData.getSellLimitOrders());
    } else if (limitOrder.getDirection() == Direction.SELL) {
      log.debug("Order: {} will be processed as Sell order", limitOrder.getOrderId());
      processDirectedLimitOrder(marketState.getTrades(), limitOrder, tickerData,
          tickerData.getBuyMarketOrders(),
          tickerData.getSellLimitOrders(),
          tickerData.getBuyLimitOrders());
    } else {
      log.error("Order {} has unsupported direction {} so will not be processed",
          limitOrder.getOrderId(), limitOrder.getDirection());
    }
  }

  public void processDirectedLimitOrder(List<Trade> trades, LimitOrder limitOrder,
      TickerData tickerData, SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders, SortedSet<LimitOrder> oppositeTypeLimitOrders) {
    if (marketOrders.isEmpty()) {
      log.debug("Market order queue empty, so no possible market order matches for limit order: {}",
          limitOrder.getOrderId());
      if (oppositeTypeLimitOrders.isEmpty()) {
        log.debug("Limit order queue empty, so no possible limit order matches for limit order: {}",
            limitOrder.getOrderId());
        marketUtils.queueIfGTC(limitOrder, sameTypeLimitOrders, persistenceHelper::saveOrderToDatabase);
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
          marketUtils.tryMakeTrade(trade -> persistenceHelper.addTradeToStateAndPersist(trades, trade), limitOrder, otherLimitOrder,
              otherLimitOrder.getLimit(),
              tickerData);
          orderProcessorUtils.removeOrderIfFulfilled(oppositeTypeLimitOrders, otherLimitOrder,
              persistenceHelper::deleteOrderFromDatabase);
          orderProcessorUtils
              .continueProcessingLimitOrderIfNotFulfilled(trades, limitOrder, tickerData, marketOrders,
                  sameTypeLimitOrders, oppositeTypeLimitOrders, this);
        } else {
          log.debug("Limits do not match, so no trade.");
          marketUtils.queueIfGTC(limitOrder, sameTypeLimitOrders, persistenceHelper::saveOrderToDatabase);
        }
      }
    } else {
      MarketOrder marketOrder = marketOrders.first();
      log.debug("Market order queue not empty, so trading with oldest order: {}", marketOrder
          .toString());
      marketUtils.tryMakeTrade(trade -> persistenceHelper.addTradeToStateAndPersist(trades, trade), limitOrder, marketOrder,
          limitOrder.getLimit(),
          tickerData);
      orderProcessorUtils.removeOrderIfFulfilled(marketOrders, marketOrder,
          persistenceHelper::deleteOrderFromDatabase);
      orderProcessorUtils
          .continueProcessingLimitOrderIfNotFulfilled(trades, limitOrder, tickerData, marketOrders,
              sameTypeLimitOrders, oppositeTypeLimitOrders, this);

    }
  }
}
