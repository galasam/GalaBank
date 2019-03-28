package com.gala.sam.tradeEngine.service;

import com.gala.sam.tradeEngine.domain.*;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.*;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import com.gala.sam.tradeEngine.utils.ConcreteOrderGenerator;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.utils.OrderProcessor.OrderProcessorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
public class MarketService {


  private MarketState marketState = new MarketState();

  private final ConcreteOrderGenerator concreteOrderGenerator;
  private final TradeRepository tradeRepository;
  private final OrderRepository orderRepository;
  private final OrderProcessorFactory orderProcessorFactory;

  public MarketService(TradeRepository tradeRepository,
                       OrderRepository orderRepository,
                       ConcreteOrderGenerator concreteOrderGenerator,
                       OrderProcessorFactory orderProcessorFactory) {
    this.tradeRepository = tradeRepository;
    this.orderRepository = orderRepository;
    this.concreteOrderGenerator = concreteOrderGenerator;
    this.orderProcessorFactory = orderProcessorFactory;
  }

  @PostConstruct
  void init() {
    log.info("Getting existing trades from database");
    marketState.getTrades().addAll((Collection<Trade>) tradeRepository.findAll());
    marketState.setTradeAddSubscriber(trade -> tradeRepository.save(trade));

    Iterable<Order> ordersFromDatabase = orderRepository.findAll();
    for (Order order : ordersFromDatabase) {
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

  public Order enterOrder(com.gala.sam.tradeEngine.domain.OrderReq.Order orderReq) {
    log.info("Processing Triggered Stop Orders");

    Order order = concreteOrderGenerator.getConcreteOrder(orderReq);

    processOrder(order);
    processTriggeredStopOrders();
    return order;
  }

  public List<Trade> getAllMatchedTrades() {
    return marketState.getTrades();
  }

  private void processOrder(Order order) {
    log.info(String.format("Processing order %s", order.toString()));

    orderProcessorFactory.getOrderProcessor(marketState, order.getType())
            .process(order);


    log.info("Ticker queues: " + marketState.getTickerQueues().toString());
    log.info("Stop Orders: " + marketState.getStopOrders().toString());
    log.info("Trades: " + marketState.getTrades().toString());
  }

  private void processTriggeredStopOrders() {
    Iterator<StopOrder> it = marketState.getStopOrders().iterator();
    while(it.hasNext()) {
      StopOrder stopOrder = it.next();
      log.info("Testing Trigger on: " + stopOrder.toString());
      if(isStopLossTriggered(stopOrder)) {
        log.info("Stop Order Triggered");
        it.remove();
        ActiveOrder activeOrder = stopOrder.toActiveOrder();
        processOrder(activeOrder);
      } else {
        log.info("Stop Order not Triggered");
      }
    }
  }

  private boolean isStopLossTriggered(StopOrder stopOrder) {
    ActiveOrder activeOrder = stopOrder.toActiveOrder();
    Optional<Float> lastExec = marketState.getTickerQueueGroup(activeOrder).getLastExecutedTradePrice();
    log.debug("Checking if there has been a previous trade");
    if(lastExec.isPresent()) {
      log.debug("Previous trade found, checking direction");
      if(activeOrder.getDirection().equals(DIRECTION.BUY)) {
        log.debug("Buy direction: testing trigger");
        return stopOrder.getTriggerPrice() <= lastExec.get();
      } else if(activeOrder.getDirection().equals(DIRECTION.SELL)) {
        log.debug("Sell direction: testing trigger");
        return stopOrder.getTriggerPrice() >= lastExec.get();
      } else {
        throw new UnsupportedOperationException("Order direction not supported");
      }
    } else {
      log.debug("No previous trade found");
      return false;
    }
  }

    public PublicMarketStatus getStatus() {
        class TickerProcessorHelper {
            List<PublicMarketStatus.Ticker> tickers = new ArrayList<>();

            void processTicker(String name, TickerData data) {
                SortedSet<ActiveOrder> buyOrders = new TreeSet<>(Comparator.comparingInt(ActiveOrder::getOrderId));
                buyOrders.addAll(data.getBuyLimitOrders());
                buyOrders.addAll(data.getBuyMarketOrders());

                SortedSet<ActiveOrder> sellOrders = new TreeSet<>(Comparator.comparingInt(ActiveOrder::getOrderId));
                sellOrders.addAll(data.getSellLimitOrders());
                sellOrders.addAll(data.getSellMarketOrders());

                if (!(buyOrders.isEmpty() && sellOrders.isEmpty())) {
                    tickers.add(PublicMarketStatus.Ticker.builder()
                            .name(name)
                            .buy(new ArrayList<>(buyOrders))
                            .sell(new ArrayList<>(sellOrders))
                            .build());
                }
            }
        }
        TickerProcessorHelper h = new TickerProcessorHelper();
        marketState.getTickerQueues().forEach(h::processTicker);
        return PublicMarketStatus.builder()
                .trades(getAllMatchedTrades())
                .orders(h.tickers)
                .build();
    }
}
