package com.gala.sam.tradeEngine.domain.OrderReq;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

public class MarketOrder extends ActiveOrder {

    @Builder
    public MarketOrder(int clientId, DIRECTION direction, int quantity,
                       TIME_IN_FORCE timeInForce, String ticker) {
        super(OrderType.ACTIVE_MARKET, clientId, direction, quantity, timeInForce, ticker);
    }

    @Override
    public Order toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.MarketOrder.builder()
                .orderId(orderId)
                .clientId(getClientId())
                .direction(getDirection())
                .quantity(getQuantity())
                .ticker(getTicker())
                .timeInForce(getTimeInForce())
                .build();
    }

}
