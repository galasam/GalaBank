package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import com.gala.sam.tradeEngine.domain.OrderReq.ReadyOrder.*;

@NonFinal
@Value
@EqualsAndHashCode(callSuper=false)
public class ReadyOrder extends Order {

    com.gala.sam.tradeEngine.domain.OrderReq.ReadyOrder.DIRECTION direction;

    public ReadyOrder(int orderId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        super(orderId);
        this.direction = direction;
        this.quantity = quantity;
        this.timeInForce = timeInForce;
        this.ticker = ticker;
    }

    int quantity;
    com.gala.sam.tradeEngine.domain.OrderReq.ReadyOrder.TIME_IN_FORCE timeInForce;
    String ticker;

}
