package com.gala.sam.tradeengine.domain.datastructures;

import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractActiveOrder;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractStopOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class MarketState {

  public static MarketState injectStopOrders(List<AbstractStopOrder> stopOrders) {
    MarketState marketState = new MarketState();
    marketState.stopOrders = stopOrders;
    return marketState;
  }

  public void reset() {
    trades.clear();
    getTickerQueues().clear();
    stopOrders.clear();
  }

  private List<Trade> trades = new ArrayList<>();
  private Map<String, TickerData> tickerQueues = new TreeMap<>();
  private List<AbstractStopOrder> stopOrders = new LinkedList<>();

  public Map<String, TickerData> getTickerQueues() {
    return tickerQueues;
  }

  public List<AbstractStopOrder> getStopOrders() {
    return stopOrders;
  }

  public List<Trade> getTrades() {
    return trades;
  }

  public TickerData getTickerQueueGroup(AbstractActiveOrder marketOrder) {
    TickerData queues = tickerQueues.get(marketOrder.getTicker());
    if (queues == null) {
      queues = new TickerData(marketOrder.getTicker());
      tickerQueues.put(marketOrder.getTicker(), queues);
    }
    return queues;
  }

}
