package com.gala.sam.tradeEngine.utils.enteredOrderGenerators;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;

public interface IEnteredOrderGenerator<T extends AbstractOrderRequest, S extends AbstractOrder> {

  S generateConcreteOrder(T orderRequest);
}
