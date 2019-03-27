package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.ReadyOrder;
import com.gala.sam.tradeEngine.domain.ReadyOrder.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;

@Slf4j
public class MarketUtils {

    public static <T extends ReadyOrder> void queueIfTimeInForce(T order,
                                                                 SortedSet<T> sameTypeLimitOrders) {
        if(order.getTimeInForce().equals(TIME_IN_FORCE.GTC)) {
            log.debug("Time in force is GTC so add to queue");
            sameTypeLimitOrders.add(order);
        } else if (order.getTimeInForce().equals(TIME_IN_FORCE.FOK)) {
            log.debug("Time in force is FOK so drop");
        } else {
            throw new UnsupportedOperationException("TIME IN FORCE mode not supported");
        }
    }

    public static void makeTrade(MarketState marketState, ReadyOrder a, ReadyOrder b, float limit, TickerData ticketData) {
        ticketData.setLastExecutedTradePrice(limit);
        if(a.getDirection().equals(ReadyOrder.DIRECTION.BUY)) {
            Trade trade = Trade.builder()
                    .buyOrder(a.getOrderId())
                    .sellOrder(b.getOrderId())
                    .matchQuantity(a.getQuantity())
                    .matchPrice(limit)
                    .build();
            log.debug("Making Buy trade: " + trade.toString());
            marketState.getTrades().add(trade);
        } else if(a.getDirection().equals(ReadyOrder.DIRECTION.SELL)) {
            Trade trade = Trade.builder()
                    .buyOrder(b.getOrderId())
                    .sellOrder(a.getOrderId())
                    .matchQuantity(a.getQuantity())
                    .matchPrice(limit)
                    .build();
            log.debug("Making Sell trade: " + trade.toString());
            marketState.getTrades().add(trade);
        } else {
            throw new UnsupportedOperationException("Order direction not supported");
        }
    }

}
