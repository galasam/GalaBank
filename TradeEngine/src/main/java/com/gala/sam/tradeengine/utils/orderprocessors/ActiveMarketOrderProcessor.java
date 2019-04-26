package com.gala.sam.tradeengine.utils.orderprocessors;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.domain.datastructures.MarketState;
import com.gala.sam.tradeengine.domain.datastructures.TickerData;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeengine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.utils.MarketUtils;
import com.gala.sam.tradeengine.utils.orderprocessors.OrderProcessorUtils.MarketOrderProcessingContinuer;
import java.util.SortedSet;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActiveMarketOrderProcessor extends AbstractOrderProcessor<MarketOrder> {

  private final OrderProcessorUtils orderProcessorUtils;

  ActiveMarketOrderProcessor(MarketUtils marketUtils,
      IOrderRepository orderRepository,
      ITradeRepository tradeRepository,
      OrderProcessorUtils orderProcessorUtils) {
    super(marketUtils, orderRepository, tradeRepository);
    this.orderProcessorUtils = orderProcessorUtils;
  }

  @Override
  public void process(MarketState marketState, MarketOrder order) {
    log.debug("Order: {} processed as Active Market order", order.getOrderId());
    ActiveMarketOrderProcess process = new ActiveMarketOrderProcess(marketState, order);
    process.start();
  }

  class ActiveMarketOrderProcess implements MarketOrderProcessingContinuer {

    private final MarketState marketState;
    private final MarketOrder marketOrder;
    private final TickerData tickerData;

    ActiveMarketOrderProcess(
        MarketState marketState, MarketOrder marketOrder) {
      this.marketState = marketState;
      this.marketOrder = marketOrder;
      this.tickerData = marketState.getTickerQueueGroup(marketOrder);
    }

    public void start() {
      if (marketOrder.getDirection() == Direction.BUY) {
        log.debug("Order: {} processed as Buy order", marketOrder.getOrderId());
        processDirectedMarketOrder(tickerData.getSellLimitOrders(),
            tickerData.getBuyMarketOrders());
      } else if (marketOrder.getDirection() == Direction.SELL) {
        log.debug("Order: {} processed as Sell order", marketOrder.getOrderId());
        processDirectedMarketOrder(tickerData.getBuyLimitOrders(),
            tickerData.getSellMarketOrders());
      } else {
        log.error("Order {} has unsupported direction {} so will not be processed",
            marketOrder.getOrderId(), marketOrder.getDirection());
      }
    }

    void processDirectedMarketOrder(SortedSet<LimitOrder> limitOrders,
        SortedSet<MarketOrder> marketOrders) {
      if (limitOrders.isEmpty()) {
        log.debug("Limit order queue empty so no possible limit order matches for market order: {}",
            marketOrder.getOrderId());
        marketUtils.queueIfGTC(marketOrder, marketOrders, persistenceHelper::saveOrderToDatabase);
      } else {
        LimitOrder limitOrder = limitOrders.first();
        log.debug("Limit order queue not empty, so trading with best limit order: {}",
            limitOrder.toString());
        Consumer<Trade> saveTrade = trade -> persistenceHelper
            .addTradeToStateAndPersist(marketState.getTrades(), trade);
        marketUtils
            .tryMakeTrade(saveTrade, marketOrder, limitOrder, limitOrder.getLimit(), tickerData);
        orderProcessorUtils.removeOrderIfFulfilled(limitOrders, limitOrder,
            persistenceHelper::deleteOrderFromDatabase);
        orderProcessorUtils
            .continueProcessingMarketOrderIfNotFulfilled(marketOrder, this);
      }
    }
  }
}
