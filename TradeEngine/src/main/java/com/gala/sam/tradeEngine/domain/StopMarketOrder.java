package com.gala.sam.tradeEngine.domain;

import lombok.Builder;
import lombok.Value;

@Value
public class StopMarketOrder extends StopOrder {

    @Builder
    public StopMarketOrder(int orderId, int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
        super(orderId, groupId, direction, quantity, timeInForce, ticker, triggerPrice);
    }

    @Override
    public MarketOrder getReadyOrder() {
        return MarketOrder.builder()
                .orderId(getOrderId())
                .direction(getDirection())
                .quantity(getQuantity())
                .timeInForce(getTimeInForce())
                .ticker(getTicker())
                .build();
    }
}
