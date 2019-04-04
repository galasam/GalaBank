package com.gala.sam.tradeEngine.utils;

import static com.gala.sam.tradeEngine.utils.orderProcessors.AbstractOrderProcessor.FAILURE;
import static com.gala.sam.tradeEngine.utils.orderProcessors.AbstractOrderProcessor.SUCCESS;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractActiveOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractStopOrder;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderTimeInForceNotSupportedException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarketUtils {

  public static <T extends AbstractActiveOrder> void queueIfTimeInForce(T order,
      SortedSet<T> sameTypeLimitOrders, Consumer<AbstractOrder> saveOrder)
      throws OrderTimeInForceNotSupportedException {
    if (order.getTimeInForce().equals(TimeInForce.GTC)) {
      log.debug("Time in force is GTC so add to queue");
      sameTypeLimitOrders.add(order);
      saveOrder.accept(order);
    } else if (order.getTimeInForce().equals(TimeInForce.FOK)) {
      log.debug("Time in force is FOK so drop");
    } else {
      throw new OrderTimeInForceNotSupportedException(order.getTimeInForce());
    }
  }

  public static void makeTrade(MarketState marketState, AbstractActiveOrder a, AbstractActiveOrder b, float limit,
      TickerData ticketData, Consumer<Trade> saveTrade) throws OrderDirectionNotSupportedException {
    log.debug("Setting last executed price of {} as {}", ticketData.getName(), ticketData.getLastExecutedTradePrice());
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
      throw new OrderDirectionNotSupportedException(a.getDirection());
    }
    marketState.getTrades().add(trade);
    saveTrade.accept(trade);
  }

  public static void updateMarketStateFromOrderRepository(MarketState marketState,
      IOrderRepository orderRepository) {
    Iterable<AbstractOrder> ordersFromDatabase = orderRepository
        .findAll();
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
              log.error("Unsupported direction {} on order {}", order.getDirection(), order.getOrderId());
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
              log.error("Unsupported direction {} on order {}", order.getDirection(), order.getOrderId());
          }
          break;
        default:
          log.error("Unsupported type {} on order {}", order.getType(), order.getOrderId());
      }
    }
  }

  public static void updateMarketStateFromTradeRepository(MarketState marketState,
      ITradeRepository tradeRepository) {
    marketState.getTrades().addAll((Collection<Trade>) tradeRepository.findAll());
  }

}
