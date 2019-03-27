package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import lombok.Builder;
import lombok.Value;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.OrderType;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;

@Value
public class MarketOrder extends ReadyOrder {

    @Builder
    public MarketOrder(int orderId, int groupId, DIRECTION direction, int quantity,
                       TIME_IN_FORCE timeInForce, String ticker) {
        super(OrderType.READY_MARKET, orderId, groupId, direction, quantity, timeInForce, ticker);
    }
}
