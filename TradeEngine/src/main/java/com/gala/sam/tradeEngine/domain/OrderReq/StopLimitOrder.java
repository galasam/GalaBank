package com.gala.sam.tradeEngine.domain.OrderReq;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import lombok.Builder;
import lombok.Value;

@Value
public class StopLimitOrder extends StopOrder {

    float limit;

    @Builder
    public StopLimitOrder(int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice, float limit) {
        super(clientId, direction, quantity, timeInForce, ticker, triggerPrice);
        this.limit = limit;
    }

    @Override
    public Order toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.StopLimitOrder.builder()
                .orderId(orderId)
                .clientId(getClientId())
                .direction(getDirection())
                .quantity(getQuantity())
                .ticker(getTicker())
                .timeInForce(getTimeInForce())
                .limit(getLimit())
                .triggerPrice(getTriggerPrice())
                .build();
    }

}
