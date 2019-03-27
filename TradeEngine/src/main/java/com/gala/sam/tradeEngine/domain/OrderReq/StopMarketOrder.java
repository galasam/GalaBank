package com.gala.sam.tradeEngine.domain.OrderReq;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import lombok.Builder;
import lombok.Value;

@Value
public class StopMarketOrder extends StopOrder {

    @Builder
    public StopMarketOrder(int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
        super(clientId, direction, quantity, timeInForce, ticker, triggerPrice);
    }

    @Override
    public Order toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.StopMarketOrder.builder()
                .orderId(orderId)
                .clientId(getClientId())
                .direction(getDirection())
                .quantity(getQuantity())
                .ticker(getTicker())
                .timeInForce(getTimeInForce())
                .triggerPrice(getTriggerPrice())
                .build();
    }
}
