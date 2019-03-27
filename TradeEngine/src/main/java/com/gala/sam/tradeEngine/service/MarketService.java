package com.gala.sam.tradeEngine.service;

import com.gala.sam.tradeEngine.domain.*;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.ReadyOrder;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.StopOrder;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import com.gala.sam.tradeEngine.utils.ConcreteOrderGenerator;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.utils.OrderProcessor.OrderProcessorFactory;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
public class MarketService {


  private MarketState marketState = new MarketState();
  private ConcreteOrderGenerator concreteOrderGenerator = new ConcreteOrderGenerator();

  public void clear() {
    marketState = new MarketState();
    concreteOrderGenerator = new ConcreteOrderGenerator();
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

    OrderProcessorFactory.getOrderProcessor(marketState, order.getType())
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
        ReadyOrder readyOrder = stopOrder.getReadyOrder();
        processOrder(readyOrder);
      } else {
        log.info("Stop Order not Triggered");
      }
    }
  }

  private boolean isStopLossTriggered(StopOrder stopOrder) {
    ReadyOrder readyOrder = stopOrder.getReadyOrder();
    Optional<Float> lastExec = marketState.getTickerQueueGroup(readyOrder).getLastExecutedTradePrice();
    log.debug("Checking if there has been a previous trade");
    if(lastExec.isPresent()) {
      log.debug("Previous trade found, checking direction");
      if(readyOrder.getDirection().equals(DIRECTION.BUY)) {
        log.debug("Buy direction: testing trigger");
        return stopOrder.getTriggerPrice() <= lastExec.get();
      } else if(readyOrder.getDirection().equals(DIRECTION.SELL)) {
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
                SortedSet<ReadyOrder> buyOrders = new TreeSet<>(Comparator.comparingInt(ReadyOrder::getOrderId));
                buyOrders.addAll(data.getBuyLimitOrders());
                buyOrders.addAll(data.getBuyMarketOrders());

                SortedSet<ReadyOrder> sellOrders = new TreeSet<>(Comparator.comparingInt(ReadyOrder::getOrderId));
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
