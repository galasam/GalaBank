package com.gala.sam.tradeEngine.utils.exception;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;

public class ProcessingOrderException extends Exception {

  public ProcessingOrderException(String message) {
    super(message);
  }

  public ProcessingOrderException(AbstractOrder order, Throwable throwable) {
    super("Processing Order: " + order.getOrderId()
        + "was not fully completed as an exception was thrown: " + throwable.getMessage()
        + "Stack Trace: " + throwable.getStackTrace());
  }
}
