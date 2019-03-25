package com.gala.sam.tradeEngine.domain.OrderReq;

import lombok.Builder;


public class MarketOrder extends ReadyOrder {

    @Builder
    public MarketOrder(DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(direction, quantity, timeInForce, ticker);
    }

    @Override
    public com.gala.sam.tradeEngine.domain.ConcreteOrder.MarketOrder toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.MarketOrder.builder()
                .orderId(orderId)
                .direction(getDirection())
                .quantity(getQuantity())
                .timeInForce(getTimeInForce())
                .ticker(getTicker())
                .build();
    }

}
