package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.OrderReq.Order;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import lombok.extern.slf4j.Slf4j;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.ActiveOrder;

import java.util.SortedSet;

@Slf4j
public class MarketUtils {

    public static <T extends ActiveOrder> void queueIfTimeInForce(T order,
                                                                  SortedSet<T> sameTypeLimitOrders) {
        if(order.getTimeInForce().equals(Order.TIME_IN_FORCE.GTC)) {
            log.debug("Time in force is GTC so add to queue");
            sameTypeLimitOrders.add(order);
        } else if (order.getTimeInForce().equals(Order.TIME_IN_FORCE.FOK)) {
            log.debug("Time in force is FOK so drop");
        } else {
            throw new UnsupportedOperationException("TIME IN FORCE mode not supported");
        }
    }

    public static void makeTrade(MarketState marketState, ActiveOrder a, ActiveOrder b, float limit, TickerData ticketData) {
        ticketData.setLastExecutedTradePrice(limit);
        int tradeQuantity = Math.min(a.getQuantity(), b.getQuantity());
        a.reduceQuantityRemaining(tradeQuantity);
        b.reduceQuantityRemaining(tradeQuantity);
        if(a.getDirection().equals(Order.DIRECTION.BUY)) {
            Trade trade = Trade.builder()
                    .buyOrder(a.getOrderId())
                    .sellOrder(b.getOrderId())
                    .matchQuantity(tradeQuantity)
                    .matchPrice(limit)
                    .ticker(a.getTicker())
                    .build();
            log.debug("Making Buy trade: " + trade.toString());
            marketState.addTrade(trade);
        } else if(a.getDirection().equals(Order.DIRECTION.SELL)) {
            Trade trade = Trade.builder()
                    .buyOrder(b.getOrderId())
                    .sellOrder(a.getOrderId())
                    .matchQuantity(tradeQuantity)
                    .matchPrice(limit)
                    .ticker(a.getTicker())
                    .build();
            log.debug("Making Sell trade: " + trade.toString());
            marketState.addTrade(trade);
        } else {
            throw new UnsupportedOperationException("Order direction not supported");
        }
    }

}
