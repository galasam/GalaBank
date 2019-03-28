package com.gala.sam.orderCapture.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotEnteredException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public OrderNotEnteredException(String order) {
    super("Order not entered: " + order.toString());
  }
}
