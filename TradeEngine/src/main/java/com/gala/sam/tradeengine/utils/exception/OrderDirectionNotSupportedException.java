package com.gala.sam.tradeengine.utils.exception;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;

public class OrderDirectionNotSupportedException extends AbstractOrderFieldNotSupportedException {

  public OrderDirectionNotSupportedException(Direction direction) {
    super("Direction not supported: " + direction.toString());
  }
}
