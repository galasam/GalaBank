package com.gala.sam.tradeEngine.domain;

public abstract class ReadyOrder extends Order {

    public ReadyOrder(OrderType type, int orderId, int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(type, orderId, groupId, direction, quantity, timeInForce, ticker);
    }

}
