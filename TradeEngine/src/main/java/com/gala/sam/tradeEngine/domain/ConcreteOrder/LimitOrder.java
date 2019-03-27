package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.OrderType;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper    = false)
public class LimitOrder extends ReadyOrder {

    float limit;

    @Builder
    public LimitOrder(int orderId, int groupId, DIRECTION direction, int quantity,
        TIME_IN_FORCE timeInForce, String ticker, float limit) {
        super(OrderType.READY_LIMIT, orderId, groupId, direction, quantity, timeInForce, ticker);
        this.limit = limit;
    }

    public boolean limitMatches(LimitOrder other) {
        if(getDirection().equals(DIRECTION.BUY)) {
            return getLimit() >= other.getLimit();
        } else if(getDirection().equals(DIRECTION.SELL)) {
            return getLimit() <= other.getLimit();
        } else {
            throw new UnsupportedOperationException("Order direction not supported");
        }
    }

}
