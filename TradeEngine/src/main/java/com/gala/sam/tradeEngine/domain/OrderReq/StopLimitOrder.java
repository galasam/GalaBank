package com.gala.sam.tradeEngine.domain.OrderReq;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import lombok.Builder;
import lombok.Value;

@Value
public class StopLimitOrder extends StopOrder {

    float limit;

    @Builder
    public StopLimitOrder(int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice, float limit) {
        super(groupId, direction, quantity, timeInForce, ticker, triggerPrice);
        this.limit = limit;
    }

    @Override
    public Order toConcrete(int orderId) {
        return com.gala.sam.tradeEngine.domain.ConcreteOrder.StopLimitOrder.builder()
                .orderId(orderId)
                .groupId(getGroupId())
                .direction(getDirection())
                .quantity(getQuantity())
                .ticker(getTicker())
                .timeInForce(getTimeInForce())
                .limit(getLimit())
                .triggerPrice(getTriggerPrice())
                .build();
    }

}
