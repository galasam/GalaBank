package com.gala.sam.tradeEngine.utils.OrderProcessor;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;

import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import java.util.SortedSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveMarketOrderProcessor extends OrderProcessor {

  private final MarketState marketState;

  public ActiveMarketOrderProcessor(IOrderRepository orderRepository,
      ITradeRepository tradeRepository, MarketState marketState) {
    super(orderRepository, tradeRepository);
    this.marketState = marketState;
  }

  @Override
  public <T extends AbstractOrder> void process(T order) {
    processMarketOrder((MarketOrder) order);
  }

  private void processMarketOrder(MarketOrder marketOrder) {
    TickerData tickerData = marketState.getTickerQueueGroup(marketOrder);
    if (marketOrder.getDirection() == Direction.BUY) {
      processDirectedMarketOrder(marketOrder, tickerData,
          tickerData.getSellLimitOrders(), tickerData.getBuyMarketOrders());
    } else if (marketOrder.getDirection() == Direction.SELL) {
      processDirectedMarketOrder(marketOrder, tickerData,
          tickerData.getBuyLimitOrders(), tickerData.getSellMarketOrders());
    } else {
      throw new UnsupportedOperationException("orderrequest direction not supported");
    }
  }

  private void processDirectedMarketOrder(MarketOrder marketOrder, TickerData tickerData,
      SortedSet<LimitOrder> limitOrders, SortedSet<MarketOrder> marketOrders) {
    log.debug("Checking Limit orderrequest queue");
    if (limitOrders.isEmpty()) {
      log.debug("Limit orderrequest queue empty, so check if time in force");
      queueIfTimeInForce(marketOrder, marketOrders, this::saveOrder);
    } else {
      LimitOrder limitOrder = limitOrders.first();
      log.debug("Limit orderrequest queue not empty, so trading with best limit order: " + limitOrder
          .toString());
      makeTrade(marketState, marketOrder, limitOrder, limitOrder.getLimit(), tickerData,
          this::saveTrade);
      log.debug("Removing limit order if it is fully satisfied.");
      if (limitOrder.isFullyFulfilled()) {
        limitOrders.remove(limitOrder);
        deleteOrder(limitOrder);
      }
      log.debug("If new market order is not fully satisfied, continue processing it.");
      if (!marketOrder.isFullyFulfilled()) {
        processDirectedMarketOrder(marketOrder, tickerData, limitOrders, marketOrders);
      }
    }
  }

}
