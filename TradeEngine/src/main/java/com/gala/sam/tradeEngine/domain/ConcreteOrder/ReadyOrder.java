package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import com.gala.sam.tradeEngine.domain.OrderReq.Order.OrderType;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;

public abstract class ReadyOrder extends Order {

    public ReadyOrder(OrderType type, int orderId, int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(type, orderId, groupId, direction, quantity, timeInForce, ticker);
    }

}
