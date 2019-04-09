package com.gala.sam.tradeEngine.utils.exception;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;

public class OrderTypeNotSupportedException extends AbstractOrderFieldNotSupportedException {

  public OrderTypeNotSupportedException(OrderType type) {
    super("Cannot handle type: " + type.toString());
  }
}
