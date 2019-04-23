package com.gala.sam.tradeengine.utils.exception;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.OrderType;

public class OrderTypeNotSupportedException extends AbstractOrderFieldNotSupportedException {

  public OrderTypeNotSupportedException(OrderType type) {
    super("Cannot handle type: " + type.toString());
  }
}
