package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.enteredorder.ActiveOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.Order;
import com.gala.sam.tradeEngine.domain.enteredorder.StopOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import java.util.Collection;
import java.util.SortedSet;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarketUtils {

  public static <T extends ActiveOrder> void queueIfTimeInForce(T order,
      SortedSet<T> sameTypeLimitOrders, Consumer<Order> saveOrder) {
    if (order.getTimeInForce().equals(TIME_IN_FORCE.GTC)) {
      log.debug("Time in force is GTC so add to queue");
      sameTypeLimitOrders.add(order);
      saveOrder.accept(order);
    } else if (order.getTimeInForce().equals(TIME_IN_FORCE.FOK)) {
      log.debug("Time in force is FOK so drop");
    } else {
      throw new UnsupportedOperationException("TIME IN FORCE mode not supported");
    }
  }

  public static void makeTrade(MarketState marketState, ActiveOrder a, ActiveOrder b, float limit,
      TickerData ticketData, Consumer<Trade> saveTrade) {
    ticketData.setLastExecutedTradePrice(limit);
    int tradeQuantity = Math.min(a.getQuantity(), b.getQuantity());
    a.reduceQuantityRemaining(tradeQuantity);
    b.reduceQuantityRemaining(tradeQuantity);
    final Trade trade;
    if (a.getDirection().equals(DIRECTION.BUY)) {
      trade = Trade.builder()
          .buyOrder(a.getOrderId())
          .sellOrder(b.getOrderId())
          .matchQuantity(tradeQuantity)
          .matchPrice(limit)
          .ticker(a.getTicker())
          .build();
      log.debug("Making Buy trade: " + trade.toString());
    } else if (a.getDirection().equals(DIRECTION.SELL)) {
      trade = Trade.builder()
          .buyOrder(b.getOrderId())
          .sellOrder(a.getOrderId())
          .matchQuantity(tradeQuantity)
          .matchPrice(limit)
          .ticker(a.getTicker())
          .build();
      log.debug("Making Sell trade: " + trade.toString());
    } else {
      throw new UnsupportedOperationException("orderrequest direction not supported");
    }
    marketState.getTrades().add(trade);
    saveTrade.accept(trade);
  }

  public static void updateMarketStateFromOrderRepository(MarketState marketState,
      OrderRepository orderRepository) {
    Iterable<com.gala.sam.tradeEngine.domain.enteredorder.Order> ordersFromDatabase = orderRepository
        .findAll();
    for (com.gala.sam.tradeEngine.domain.enteredorder.Order order : ordersFromDatabase) {
      TickerData tickerQueueGroup;
      switch (order.getType()) {
        case STOP:
          marketState.getStopOrders().add((StopOrder) order);
          break;
        case ACTIVE_LIMIT:
          LimitOrder limitOrder = (LimitOrder) order;
          tickerQueueGroup = marketState.getTickerQueueGroup(limitOrder);
          switch (limitOrder.getDirection()) {
            case BUY:
              tickerQueueGroup.getBuyLimitOrders().add(limitOrder);
              break;
            case SELL:
              tickerQueueGroup.getSellLimitOrders().add(limitOrder);
              break;
            default:
              throw new UnsupportedOperationException("Unsupported direction");
          }
          break;
        case ACTIVE_MARKET:
          MarketOrder marketOrder = (MarketOrder) order;
          tickerQueueGroup = marketState.getTickerQueueGroup(marketOrder);
          switch (marketOrder.getDirection()) {
            case BUY:
              tickerQueueGroup.getBuyMarketOrders().add(marketOrder);
              break;
            case SELL:
              tickerQueueGroup.getSellMarketOrders().add(marketOrder);
              break;
            default:
              throw new UnsupportedOperationException("Unsupported direction");
          }
          break;
        default:
          throw new UnsupportedOperationException("Unsupported direction");
      }
    }
  }

  public static void updateMarketStateFromTradeRepository(MarketState marketState,
      TradeRepository tradeRepository) {
    marketState.getTrades().addAll((Collection<Trade>) tradeRepository.findAll());
  }

}
