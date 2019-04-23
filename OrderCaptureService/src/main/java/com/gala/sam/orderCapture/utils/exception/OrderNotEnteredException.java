package com.gala.sam.orderCapture.utils.exception;

import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotEnteredException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public OrderNotEnteredException(AbstractOrderRequest order) {
    super("Order not entered: " + order.toString());
  }
}
