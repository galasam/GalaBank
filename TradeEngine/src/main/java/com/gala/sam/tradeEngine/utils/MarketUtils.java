package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.ReadyOrder;
import com.gala.sam.tradeEngine.domain.ReadyOrder.TIME_IN_FORCE;
import lombok.extern.java.Log;

import java.util.SortedSet;

@Log
public class MarketUtils {

    public static <T extends ReadyOrder> void queueIfTimeInForce(T order,
                                                                 SortedSet<T> sameTypeLimitOrders) {
        if(order.getTimeInForce().equals(TIME_IN_FORCE.GTC)) {
            log.finest("Time in force is GTC so add to queue");
            sameTypeLimitOrders.add(order);
        } else if (order.getTimeInForce().equals(TIME_IN_FORCE.FOK)) {
            log.finest("Time in force is FOK so drop");
        } else {
            throw new UnsupportedOperationException("TIME IN FORCE mode not supported");
        }
    }
}
