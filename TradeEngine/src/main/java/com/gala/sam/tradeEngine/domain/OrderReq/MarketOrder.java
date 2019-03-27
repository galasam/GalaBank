package com.gala.sam.tradeEngine.domain.OrderReq;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import lombok.Builder;
import lombok.Value;

@Value
public class MarketOrder extends ReadyOrder {

    @Builder
    public MarketOrder(int groupId, DIRECTION direction, int quantity,
                       TIME_IN_FORCE timeInForce, String ticker) {
        super(OrderType.READY_MARKET, groupId, direction, quantity, timeInForce, ticker);
    }

    @Override
    public Order toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.MarketOrder.builder()
                .orderId(orderId)
                .groupId(getGroupId())
                .direction(getDirection())
                .quantity(getQuantity())
                .ticker(getTicker())
                .timeInForce(getTimeInForce())
                .build();
    }

}
