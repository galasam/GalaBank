package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import org.springframework.stereotype.Component;

@Component
public class ConcreteOrderGenerator {

    private static final int InitialOrderIndex = 1;
    private int currentOrderIndex = InitialOrderIndex;

    private void incrementOrderIndex() {
        currentOrderIndex++;
    }

    public Order getConcreteOrder(com.gala.sam.tradeEngine.domain.OrderReq.Order orderReq) {
        Order concreteOrder =  orderReq.toConcrete(currentOrderIndex);
        incrementOrderIndex();
        return concreteOrder;
    }

}
