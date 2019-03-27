package com.gala.sam.tradeEngine.domain;

import lombok.Builder;


public class MarketOrder extends ReadyOrder {

    @Builder
    public MarketOrder(int orderId, int groupId, DIRECTION direction, int quantity,
                       TIME_IN_FORCE timeInForce, String ticker) {
        super(OrderType.READY_MARKET, orderId, groupId, direction, quantity, timeInForce, ticker);
    }
}
