package com.gala.sam.tradeEngine.domain.OrderReq;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;

@NonFinal
@Value
@EqualsAndHashCode(callSuper=false)
abstract public class ReadyOrder extends Order {
    public enum DIRECTION {SELL, BUY}
    public enum TIME_IN_FORCE {FOK, GTC}

    DIRECTION direction;
    int quantity;
    TIME_IN_FORCE timeInForce;
    String ticker;

    @Override
    public abstract com.gala.sam.tradeEngine.domain.ConcreteOrder.ReadyOrder toConcrete(int orderId);
}
