package com.gala.sam.tradeEngine.utils.exception;

import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest.Direction;

public class OrderDirectionNotSupportedException extends AbstractOrderFieldNotSupportedException {

  public OrderDirectionNotSupportedException(Direction direction) {
    super("Direction not supported: " + direction.toString());
  }
}
