package com.gala.sam.tradeEngine.utils.enteredOrderGenerators;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;

public interface EnteredOrderGenerator<T extends AbstractOrderRequest, S extends AbstractOrder> {
  S generateConcreteOrder(T orderRequest);
}
