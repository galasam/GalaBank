package com.gala.sam.tradeEngine.domain.dataStructures;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.ReadyOrder;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.StopOrder;
import com.gala.sam.tradeEngine.domain.Trade;

import java.util.*;

public class MarketState {

    private List<Trade> trades = new ArrayList<>();
    private Map<String, TickerData> tickerQueues = new TreeMap<>();
    private List<StopOrder> stopOrders = new LinkedList<>();


    public Map<String, TickerData> getTickerQueues() {
        return tickerQueues;
    }

    public List<StopOrder> getStopOrders() {
        return stopOrders;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public TickerData getTickerQueueGroup(ReadyOrder marketOrder) {
        TickerData queues = tickerQueues.get(marketOrder.getTicker());
        if (queues == null) {
            queues = new TickerData();
            tickerQueues.put(marketOrder.getTicker(), queues);
        }
        return queues;
    }
}
