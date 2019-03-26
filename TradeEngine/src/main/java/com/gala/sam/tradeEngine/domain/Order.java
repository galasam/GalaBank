package com.gala.sam.tradeEngine.domain;

import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public abstract class Order {

    public enum DIRECTION {SELL, BUY}
    public enum TIME_IN_FORCE {FOK, GTC}

    int orderId;
    DIRECTION direction;
    int quantity;
    TIME_IN_FORCE timeInForce;
    String ticker;

}
