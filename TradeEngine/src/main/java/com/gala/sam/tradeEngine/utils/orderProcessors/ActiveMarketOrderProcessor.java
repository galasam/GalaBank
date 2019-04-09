package com.gala.sam.tradeEngine.utils.orderProcessors;

import static com.gala.sam.tradeEngine.utils.MarketUtils.tryMakeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfGTC;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveMarketOrderProcessor extends AbstractOrderProcessor<MarketOrder> {

  private final MarketState marketState;

  public ActiveMarketOrderProcessor(IOrderRepository orderRepository,
      ITradeRepository tradeRepository, MarketState marketState) {
    super(orderRepository, tradeRepository);
    this.marketState = marketState;
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

  private void processDirectedMarketOrder(MarketOrder marketOrder, TickerData tickerData,
      SortedSet<LimitOrder> limitOrders, SortedSet<MarketOrder> marketOrders) {
    if (limitOrders.isEmpty()) {
      log.debug("Limit order queue empty so no possible limit order matches for market order: {}", marketOrder.getOrderId());
      queueIfGTC(marketOrder, marketOrders, this::saveOrder);
    } else {
      LimitOrder limitOrder = limitOrders.first();
      log.debug("Limit order queue not empty, so trading with best limit order: {}",
          limitOrder.toString());
      tryMakeTrade(marketState, marketOrder, limitOrder, limitOrder.getLimit(), tickerData,
          this::saveTrade);
      if (limitOrder.isFullyFulfilled()) {
        log.debug("Limit order {} is fully satisfied so removing", limitOrder.getOrderId());
        limitOrders.remove(limitOrder);
        deleteOrder(limitOrder);
      }
      if (!marketOrder.isFullyFulfilled()) {
        log.debug("New market order {} is not fully satisfied so continue processing .",
            marketOrder);
        processDirectedMarketOrder(marketOrder, tickerData, limitOrders, marketOrders);
      }
    }
  }

}
