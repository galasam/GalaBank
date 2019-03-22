package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.Order;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;

public class OrderProcessorFactory {
    public static OrderProcessor getOrderProcessor(MarketState marketState, Order.OrderType type) {
        switch(type) {
            case STOP:
                return new StopOrderProcessor(marketState);
            case READY_LIMIT:
                return new ReadyLimitOrderProcessor(marketState);
            case READY_MARKET:
                return new ReadyMarketOrderProcessor(marketState);
            default:
                throw new UnsupportedOperationException("Order type not specified");
        }
    }
}
