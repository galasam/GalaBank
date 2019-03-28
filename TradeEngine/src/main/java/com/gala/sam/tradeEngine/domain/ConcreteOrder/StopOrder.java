package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import com.gala.sam.tradeEngine.domain.OrderReq.Order.OrderType;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "StopOrder")
@DiscriminatorValue("StopOrder")
@NoArgsConstructor
public abstract class StopOrder extends Order {

    @Column
    float triggerPrice;

    public StopOrder(int orderId, int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
        super(OrderType.STOP, orderId, clientId, direction, quantity, timeInForce, ticker);
        this.triggerPrice = triggerPrice;
    }

    public float getTriggerPrice() {
        return triggerPrice;
    }

    public abstract ActiveOrder toActiveOrder();

}
