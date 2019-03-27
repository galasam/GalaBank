package com.gala.sam.tradeEngine.domain.OrderReq;

public abstract class ActiveOrder extends Order {

    public ActiveOrder(OrderType type, int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(type, groupId, direction, quantity, timeInForce, ticker);
    }

}
