package com.gala.sam.tradeEngine.domain.OrderReq;

public abstract class ReadyOrder extends Order {

    public ReadyOrder(OrderType type, int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(type, groupId, direction, quantity, timeInForce, ticker);
    }

}
