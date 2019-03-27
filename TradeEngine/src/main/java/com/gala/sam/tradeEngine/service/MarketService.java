package com.gala.sam.tradeEngine.service;

import com.gala.sam.tradeEngine.domain.*;
import com.gala.sam.tradeEngine.domain.ReadyOrder.DIRECTION;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import com.gala.sam.tradeEngine.utils.OrderProcessor.OrderProcessorFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;

@Slf4j
public class MarketService {


  private MarketState marketState = new MarketState();

  public void clear() {
    marketState = new MarketState();
  }

  public void enterOrder(Order order) {
    log.info("Processing Triggered Stop Orders");
    processOrder(order);
    processTriggeredStopOrders();
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
}
