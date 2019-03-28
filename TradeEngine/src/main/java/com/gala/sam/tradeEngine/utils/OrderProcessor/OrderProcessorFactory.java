package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.OrderReq.Order.OrderType;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessorFactory {
    public OrderProcessor getOrderProcessor(MarketState marketState, OrderType type) {
        switch(type) {
            case STOP:
                return new StopOrderProcessor(marketState);
            case ACTIVE_LIMIT:
                return new ActiveLimitOrderProcessor(marketState);
            case ACTIVE_MARKET:
                return new ActiveMarketOrderProcessor(marketState);
            default:
                throw new UnsupportedOperationException("Order type not specified");
        }
    }
}
