package com.gala.sam.tradeEngine.utils.exception;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;

public class OrderTimeInForceNotSupportedException extends AbstractOrderFieldNotSupportedException {

  public OrderTimeInForceNotSupportedException(TimeInForce timeInForce) {
    super("TimeInForce mode not supported: " + timeInForce.toString());
  }
}
