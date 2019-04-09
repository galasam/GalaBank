package com.gala.sam.tradeEngine.utils.orderProcessors;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfGTC;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.exception.AbstractOrderFieldNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.ProcessingActiveOrderException;
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
  public void process(MarketOrder order)
      throws ProcessingActiveOrderException {
    log.debug("Order: {} processed as Active Market order", order.getOrderId());
    processMarketOrder(order);
  }

  private void processMarketOrder(MarketOrder marketOrder)
      throws ProcessingActiveOrderException {
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
      throw new ProcessingActiveOrderException(marketOrder, new OrderDirectionNotSupportedException(marketOrder.getDirection()));
    }
  }

  private void processDirectedMarketOrder(MarketOrder marketOrder, TickerData tickerData,
      SortedSet<LimitOrder> limitOrders, SortedSet<MarketOrder> marketOrders)
      throws ProcessingActiveOrderException {
    try {
      if (limitOrders.isEmpty()) {
        log.debug("Limit order queue empty so no possible limit order matches for market order: {}", marketOrder.getOrderId());
        queueIfGTC(marketOrder, marketOrders, this::saveOrder);
      } else {
        LimitOrder limitOrder = limitOrders.first();
        log.debug("Limit order queue not empty, so trading with best limit order: {}",
            limitOrder.toString());
        makeTrade(marketState, marketOrder, limitOrder, limitOrder.getLimit(), tickerData,
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
    } catch (AbstractOrderFieldNotSupportedException e) {
      log.error("Order {} has unsupported field {} so will not be processed", marketOrder.getOrderId(), marketOrder.getDirection());
      throw new ProcessingActiveOrderException(marketOrder, e);
    } catch (ProcessingActiveOrderException e) {
      log.error("There was an processing exception when the order {} was further processed so the order will not be processed any more and left in a partially processed state", marketOrder.getOrderId());
      throw new ProcessingActiveOrderException(marketOrder, e);
    }
  }

}
