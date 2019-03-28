package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq;
import org.springframework.stereotype.Component;

@Component
public class ConcreteOrderGenerator {

  private static final int InitialOrderIndex = 1;
  private int currentOrderIndex = InitialOrderIndex;

  private void incrementOrderIndex() {
    currentOrderIndex++;
  }

  public Order getConcreteOrder(OrderReq orderReq) {
    Order concreteOrder = orderReq.toConcrete(currentOrderIndex);
    incrementOrderIndex();
    return concreteOrder;
  }

}
