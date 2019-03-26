package com.gala.sam.tradeEngine.domain;

public abstract class ReadyOrder extends Order {

    public ReadyOrder(int orderId, int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(orderId, groupId, direction, quantity, timeInForce, ticker);
    }

}
