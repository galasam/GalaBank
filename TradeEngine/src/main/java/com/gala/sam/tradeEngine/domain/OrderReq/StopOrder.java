package com.gala.sam.tradeEngine.domain.OrderReq;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=false)
@Builder
public class StopOrder extends Order {

    float triggerPrice;
    ReadyOrder readyOrder;

    public com.gala.sam.tradeEngine.domain.ConcreteOrder.StopOrder toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.StopOrder.builder()
                .triggerPrice(getTriggerPrice())
                .readyOrder(getReadyOrder().toConcrete(orderId))
                .build();
    }
}
