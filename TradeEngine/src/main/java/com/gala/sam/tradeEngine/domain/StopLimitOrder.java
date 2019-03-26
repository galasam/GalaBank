package com.gala.sam.tradeEngine.domain;

import lombok.Builder;
import lombok.Value;

@Value
public class StopLimitOrder extends StopOrder {

    float limit;

    @Builder
    public StopLimitOrder(int orderId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice, float limit) {
        super(orderId, direction, quantity, timeInForce, ticker, triggerPrice);
        this.limit = limit;
    }

    @Override
    public LimitOrder getReadyOrder() {
        return LimitOrder.builder()
                .orderId(getOrderId())
                .direction(getDirection())
                .quantity(getQuantity())
                .timeInForce(getTimeInForce())
                .ticker(getTicker())
                .limit(getLimit())
                .build();
    }
}
