package com.gala.sam.tradeEngine.domain.OrderReq;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper    = false)
public class LimitOrder extends ReadyOrder {

    float limit;

    @Builder
    public LimitOrder(DIRECTION direction, int quantity,
        TIME_IN_FORCE timeInForce, String ticker, float limit) {
        super(direction, quantity, timeInForce, ticker);
        this.limit = limit;
    }

    @Override
    public com.gala.sam.tradeEngine.domain.ConcreteOrder.LimitOrder toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.LimitOrder.builder()
                .orderId(orderId)
                .direction(getDirection())
                .quantity(getQuantity())
                .timeInForce(getTimeInForce())
                .ticker(getTicker())
                .limit(getLimit())
                .build();
    }
}
