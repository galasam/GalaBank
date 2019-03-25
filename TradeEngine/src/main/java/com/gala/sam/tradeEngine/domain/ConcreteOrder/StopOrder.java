package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=false)
public class StopOrder extends Order {

    @Builder
    public StopOrder(int orderId, float triggerPrice, ReadyOrder readyOrder) {
        super(orderId);
        this.triggerPrice = triggerPrice;
        this.readyOrder = readyOrder;
    }

    float triggerPrice;
    ReadyOrder readyOrder;
}
