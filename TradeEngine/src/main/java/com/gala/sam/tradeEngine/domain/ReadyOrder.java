package com.gala.sam.tradeEngine.domain;

public abstract class ReadyOrder extends Order {

    public ReadyOrder(int orderId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(orderId, direction, quantity, timeInForce, ticker);
    }
}
