package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.StopOrder;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class StopOrderProcessor implements OrderProcessor {

    MarketState marketState;

    @Override
    public <T extends Order> void process(T order) {
        log.debug("Adding stop order: " + order.toString());
        marketState.getStopOrders().add((StopOrder) order);
    }
}
