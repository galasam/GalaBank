package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import org.springframework.stereotype.Component;

@Component
public class ConcreteOrderGenerator {

  private static final int InitialOrderIndex = 1;
  private int currentOrderIndex = InitialOrderIndex;

  private void incrementOrderIndex() {
    currentOrderIndex++;
  }

  public AbstractOrder getConcreteOrder(AbstractOrderRequest orderRequest) {
    AbstractOrder concreteOrder = orderRequest.toConcrete(currentOrderIndex);
    incrementOrderIndex();
    return concreteOrder;
  }

}
