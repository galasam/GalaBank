package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.enteredorder.Order;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest;
import org.springframework.stereotype.Component;

@Component
public class ConcreteOrderGenerator {

  private static final int InitialOrderIndex = 1;
  private int currentOrderIndex = InitialOrderIndex;

  private void incrementOrderIndex() {
    currentOrderIndex++;
  }

  public Order getConcreteOrder(OrderRequest orderRequest) {
    Order concreteOrder = orderRequest.toConcrete(currentOrderIndex);
    incrementOrderIndex();
    return concreteOrder;
  }

}
