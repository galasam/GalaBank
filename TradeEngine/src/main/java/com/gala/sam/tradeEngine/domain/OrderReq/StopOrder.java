package com.gala.sam.tradeEngine.domain.OrderReq;


public abstract class StopOrder extends Order {

    float triggerPrice;

    public StopOrder(int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
        super(OrderType.STOP, clientId, direction, quantity, timeInForce, ticker);
        this.triggerPrice = triggerPrice;
    }

    public float getTriggerPrice() {
        return triggerPrice;
    }

}