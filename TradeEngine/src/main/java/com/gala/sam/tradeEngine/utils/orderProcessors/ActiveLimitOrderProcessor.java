package com.gala.sam.tradeEngine.utils.orderProcessors;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;

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
import com.gala.sam.tradeEngine.utils.exception.OrderTimeInForceNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.ProcessingActiveOrderException;
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
  public void process(LimitOrder order)
      throws ProcessingActiveOrderException {
    log.debug("Order: {} will be processed as Active Limit order", order.getOrderId());
    processLimitOrder(order);
  }

  private void processLimitOrder(LimitOrder limitOrder)
      throws ProcessingActiveOrderException {
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
      throw new ProcessingActiveOrderException(limitOrder,
          new OrderDirectionNotSupportedException(limitOrder.getDirection()));
    }
  }

  private void processDirectedLimitOrder(LimitOrder limitOrder, TickerData tickerData,
      SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders,
      SortedSet<LimitOrder> oppositeTypeLimitOrders)
      throws ProcessingActiveOrderException {
    try {
      if (marketOrders.isEmpty()) {
        log.debug("Market order queue empty, so no possible market order matches for limit order: {}", limitOrder.getOrderId());
        if (oppositeTypeLimitOrders.isEmpty()) {
          log.debug("Limit order queue empty, so no possible limit order matches for limit order: {}", limitOrder.getOrderId());
          queueIfTimeInForce(limitOrder, sameTypeLimitOrders, this::saveOrder);
        } else {
          LimitOrder otherLimitOrder = oppositeTypeLimitOrders.first();
          log.debug("Limit order queue not empty, so extracted top order: {}", otherLimitOrder.toString());
          if (limitOrder.limitMatches(otherLimitOrder)) {
            log.debug("Limits match so completing trade with order: {}", otherLimitOrder.getOrderId());
              makeTrade(marketState, limitOrder, otherLimitOrder, otherLimitOrder.getLimit(),
                  tickerData, this::saveTrade);
            removeOrderIfFulfilled(oppositeTypeLimitOrders, otherLimitOrder);
            continueProcessingLimitOrderIfNotFulfilled(limitOrder, tickerData, marketOrders,
                sameTypeLimitOrders, oppositeTypeLimitOrders);
          } else {
            log.debug("Limits do not match, so no trade.");
            queueIfTimeInForce(limitOrder, sameTypeLimitOrders, this::saveOrder);
          }
        }
      } else {
        MarketOrder marketOrder = marketOrders.first();
        log.debug("Market order queue not empty, so trading with oldest order: {}", marketOrder
            .toString());
        makeTrade(marketState, marketOrder, limitOrder, limitOrder.getLimit(), tickerData,
            this::saveTrade);
        removeOrderIfFulfilled(marketOrders, marketOrder);
        continueProcessingLimitOrderIfNotFulfilled(limitOrder, tickerData, marketOrders,
            sameTypeLimitOrders, oppositeTypeLimitOrders);

      }
    } catch (AbstractOrderFieldNotSupportedException e) {
      log.error("Order {} has unsupported field {} so will not be processed", limitOrder.getOrderId(), limitOrder.getDirection());
      throw new ProcessingActiveOrderException(limitOrder, e);
    } catch (ProcessingActiveOrderException e) {
      log.error("There was an processing exception when the order {} was further processed so the order will not be processed any more and left in a partially processed state", limitOrder.getOrderId());
      throw new ProcessingActiveOrderException(limitOrder, e);
    }
  }

  private void continueProcessingLimitOrderIfNotFulfilled(LimitOrder limitOrder,
      TickerData tickerData, SortedSet<MarketOrder> marketOrders,
      SortedSet<LimitOrder> sameTypeLimitOrders, SortedSet<LimitOrder> oppositeTypeLimitOrders)
      throws ProcessingActiveOrderException {
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
