package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import com.gala.sam.tradeEngine.domain.OrderReq.Order.OrderType;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;

public abstract class StopOrder extends Order {

    float triggerPrice;

    public StopOrder(int orderId, int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
        super(OrderType.STOP, orderId, clientId, direction, quantity, timeInForce, ticker);
        this.triggerPrice = triggerPrice;
    }

    public float getTriggerPrice() {
        return triggerPrice;
    }

    public abstract ActiveOrder getActiveOrder();

}
