package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import lombok.Builder;
import lombok.Value;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.OrderType;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;

@Value
public class StopLimitOrder extends StopOrder {

    float limit;

    @Builder
    public StopLimitOrder(int orderId, int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice, float limit) {
        super(orderId, groupId, direction, quantity, timeInForce, ticker, triggerPrice);
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
