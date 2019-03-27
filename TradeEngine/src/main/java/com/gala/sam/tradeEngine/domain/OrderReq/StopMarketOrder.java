package com.gala.sam.tradeEngine.domain.OrderReq;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import lombok.Builder;
import lombok.Value;

@Value
public class StopMarketOrder extends StopOrder {

    @Builder
    public StopMarketOrder(int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
        super(groupId, direction, quantity, timeInForce, ticker, triggerPrice);
    }

    @Override
    public Order toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.StopMarketOrder.builder()
                .orderId(orderId)
                .groupId(getGroupId())
                .direction(getDirection())
                .quantity(getQuantity())
                .ticker(getTicker())
                .timeInForce(getTimeInForce())
                .triggerPrice(getTriggerPrice())
                .build();
    }
}
