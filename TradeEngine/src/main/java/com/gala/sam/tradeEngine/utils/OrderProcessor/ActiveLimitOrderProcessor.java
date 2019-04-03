package com.gala.sam.tradeEngine.utils.OrderProcessor;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;

import com.gala.sam.tradeEngine.domain.EnteredOrder.LimitOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.MarketOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import com.gala.sam.tradeEngine.domain.OrderRequest.OrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveLimitOrderProcessor extends OrderProcessor {

  final MarketState marketState;

  public ActiveLimitOrderProcessor(OrderRepository orderRepository, TradeRepository tradeRepository,
      MarketState marketState) {
    super(orderRepository, tradeRepository);
    this.marketState = marketState;
  }

  @Override
  public <T extends Order> void process(T order) {
    processLimitOrder((LimitOrder) order);
  }

  private void processLimitOrder(LimitOrder limitOrder) {
    TickerData tickerData = marketState.getTickerQueueGroup(limitOrder);
    if (limitOrder.getDirection() == DIRECTION.BUY) {
      processDirectedLimitOrder(limitOrder, tickerData,
          tickerData.getSellMarketOrders(),
          tickerData.getBuyLimitOrders(),
          tickerData.getSellLimitOrders());
    } else if (limitOrder.getDirection() == DIRECTION.SELL) {
      processDirectedLimitOrder(limitOrder, tickerData,
          tickerData.getBuyMarketOrders(),
          tickerData.getSellLimitOrders(),
          tickerData.getBuyLimitOrders());
    } else {
      throw new UnsupportedOperationException("OrderRequest direction not supported");
    }
  }

  private void processDirectedLimitOrder(LimitOrder limitOrder, TickerData tickerData,
      SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders,
      SortedSet<LimitOrder> oppositeTypeLimitOrders) {
    log.debug("Checking main.Market OrderRequest queue");
    if (marketOrders.isEmpty()) {
      log.debug("main.Market OrderRequest queue empty, so checking Limit orders");
      if (oppositeTypeLimitOrders.isEmpty()) {
        log.debug("Limit OrderRequest queue empty, so check if time in force");
        queueIfTimeInForce(limitOrder, sameTypeLimitOrders, this::saveOrder);
      } else {
        LimitOrder otherLimitOrder = oppositeTypeLimitOrders.first();
        log.debug(
            "Limit OrderRequest queue not empty, so checking if best order matches: " + otherLimitOrder
                .toString());

        if (limitOrder.limitMatches(otherLimitOrder)) {
          log.debug("Limits match so completing trade");
          makeTrade(marketState, limitOrder, otherLimitOrder, otherLimitOrder.getLimit(),
              tickerData, this::saveTrade);
          removeOrderIfFulfilled(oppositeTypeLimitOrders, otherLimitOrder);
          continueProcessingLimitOrderIfNotFulfilled(limitOrder, tickerData, marketOrders,
              sameTypeLimitOrders, oppositeTypeLimitOrders);
        } else {
          log.debug("Limits do not match, so check if time in force");
          queueIfTimeInForce(limitOrder, sameTypeLimitOrders, this::saveOrder);
        }
      }
    } else {
      log.debug("main.Market OrderRequest queue not empty, so trading with oldest order: " + limitOrder
          .toString());
      MarketOrder marketOrder = marketOrders.first();
      makeTrade(marketState, marketOrder, limitOrder, limitOrder.getLimit(), tickerData,
          this::saveTrade);
      removeOrderIfFulfilled(marketOrders, marketOrder);
      continueProcessingLimitOrderIfNotFulfilled(limitOrder, tickerData, marketOrders,
          sameTypeLimitOrders, oppositeTypeLimitOrders);
    }
  }

  private void continueProcessingLimitOrderIfNotFulfilled(LimitOrder limitOrder,
      TickerData tickerData, SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders, SortedSet<LimitOrder> oppositeTypeLimitOrders) {
    log.debug("If new limit order is not fully satisfied, continue processing it.");
    if (!limitOrder.isFullyFulfilled()) {
      processDirectedLimitOrder(limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
          oppositeTypeLimitOrders);
    }
  }

  private <T extends Order> void removeOrderIfFulfilled(SortedSet<T> orders, T order) {
    log.debug("Removing market OrderRequest if it is fully satisfied.");
    if (order.isFullyFulfilled()) {
      orders.remove(order);
      deleteOrder(order);
    }
  }

}
