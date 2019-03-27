package com.gala.sam.tradeEngine.domain;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;

@NonFinal
@Value
@EqualsAndHashCode(callSuper=false)
public class ReadyOrder extends Order {
    public enum DIRECTION {SELL, BUY}
    public enum TIME_IN_FORCE {FOK, GTC}

    int orderId;
    DIRECTION direction;
    int quantity;
    TIME_IN_FORCE timeInForce;
    String ticker;

    public ReadyOrder(OrderType type, int orderId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(type);
        this.orderId = orderId;
        this.direction = direction;
        this.quantity = quantity;
        this.timeInForce = timeInForce;
        this.ticker = ticker;
    }
}
