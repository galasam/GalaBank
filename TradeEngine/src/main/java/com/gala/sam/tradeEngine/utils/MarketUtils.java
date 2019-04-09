package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractActiveOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractStopOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import java.util.Collection;
import java.util.SortedSet;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MarketUtils {

  public static void updateMarketStateFromTradeRepository(MarketState marketState,
      ITradeRepository tradeRepository) {
    marketState.getTrades().addAll((Collection<Trade>) tradeRepository.findAll());
  }

  public <T extends AbstractActiveOrder> void queueIfGTC(T order,
      SortedSet<T> sameTypeLimitOrders, Consumer<AbstractOrder> saveOrder) {
    if (order.getTimeInForce().equals(TimeInForce.GTC)) {
      log.debug("Time in force is GTC so add to queue");
      sameTypeLimitOrders.add(order);
      saveOrder.accept(order);
    } else if (order.getTimeInForce().equals(TimeInForce.FOK)) {
      log.debug("Time in force is FOK so drop");
    } else {
      log.error("Order {} has unsupported timeInForce {} so will not be added to queue",
          order.getOrderId(), order.getTimeInForce());
    }
  }

  public void tryMakeTrade(Consumer<Trade> saveTrade, AbstractActiveOrder a, AbstractActiveOrder b,
      float limit,
      TickerData ticketData) {
    log.debug("Setting last executed price of {} as {}", ticketData.getName(),
        ticketData.getLastExecutedTradePrice());
    ticketData.setLastExecutedTradePrice(limit);
    int tradeQuantity = Math.min(a.getQuantity(), b.getQuantity());
    a.reduceQuantityRemaining(tradeQuantity);
    b.reduceQuantityRemaining(tradeQuantity);
    final Trade trade;
    if (a.getDirection().equals(Direction.BUY)) {
      log.debug("creating a trade {} buys from {}", a.getOrderId(), b.getOrderId());
      trade = Trade.builder()
          .buyOrder(a.getOrderId())
          .sellOrder(b.getOrderId())
          .matchQuantity(tradeQuantity)
          .matchPrice(limit)
          .ticker(a.getTicker())
          .build();
      log.debug("Making Buy trade: " + trade.toString());
    } else if (a.getDirection().equals(Direction.SELL)) {
      log.debug("creating a trade {} sells to {}", a.getOrderId(), b.getOrderId());
      trade = Trade.builder()
          .buyOrder(b.getOrderId())
          .sellOrder(a.getOrderId())
          .matchQuantity(tradeQuantity)
          .matchPrice(limit)
          .ticker(a.getTicker())
          .build();
      log.debug("Making Sell trade: " + trade.toString());
    } else {
      log.error("Order {} has unsupported direction {} so trade will not be created",
          a.getOrderId(), a.getDirection());
      return;
    }
    saveTrade.accept(trade);
  }

  public void updateMarketStateFromOrderRepository(MarketState marketState,
      IOrderRepository orderRepository) {
    Iterable<AbstractOrder> ordersFromDatabase = orderRepository.findAll();
    for (AbstractOrder order : ordersFromDatabase) {
      log.debug("Reading order: {}", order.getOrderId());
      TickerData tickerQueueGroup;
      switch (order.getType()) {
        case STOP_LIMIT:
        case STOP_MARKET:
          log.debug("Adding stop order {} to stop order queue", order.getOrderId());
          marketState.getStopOrders().add((AbstractStopOrder) order);
          break;
        case ACTIVE_LIMIT:
          LimitOrder limitOrder = (LimitOrder) order;
          tickerQueueGroup = marketState.getTickerQueueGroup(limitOrder);
          switch (limitOrder.getDirection()) {
            case BUY:
              log.debug("Adding limit order {} to buy queue", order.getOrderId());
              tickerQueueGroup.getBuyLimitOrders().add(limitOrder);
              break;
            case SELL:
              log.debug("Adding limit order {} to sell queue", order.getOrderId());
              tickerQueueGroup.getSellLimitOrders().add(limitOrder);
              break;
            default:
              log.error("Unsupported direction {} on order {} so order will not be loaded",
                  order.getDirection(), order.getOrderId());
          }
          break;
        case ACTIVE_MARKET:
          MarketOrder marketOrder = (MarketOrder) order;
          tickerQueueGroup = marketState.getTickerQueueGroup(marketOrder);
          switch (marketOrder.getDirection()) {
            case BUY:
              log.debug("Adding market order {} to buy queue", order.getOrderId());
              tickerQueueGroup.getBuyMarketOrders().add(marketOrder);
              break;
            case SELL:
              log.debug("Adding market order {} to sell queue", order.getOrderId());
              tickerQueueGroup.getSellMarketOrders().add(marketOrder);
              break;
            default:
              log.error("Unsupported direction {} on order {} so order will not be loaded",
                  order.getDirection(), order.getOrderId());
          }
          break;
        default:
          log.error("Unsupported type {} on order {} so order will not be loaded", order.getType(),
              order.getOrderId());
      }
    }
  }

}
