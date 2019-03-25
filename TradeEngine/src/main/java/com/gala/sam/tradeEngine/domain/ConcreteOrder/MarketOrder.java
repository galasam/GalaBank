package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import lombok.Builder;
import lombok.Value;
import com.gala.sam.tradeEngine.domain.OrderReq.ReadyOrder.*;

@Value
public class MarketOrder extends ReadyOrder {

    @Builder
    public MarketOrder(int orderId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(orderId, direction, quantity, timeInForce, ticker);
    }
}
