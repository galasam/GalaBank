package com.gala.sam.tradeEngine.utils.exception;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractActiveOrder;

public class ProcessingActiveOrderException extends ProcessingOrderException {

  public ProcessingActiveOrderException(AbstractActiveOrder order, Throwable throwable) {
    super("Processing Active Order: " + order.getOrderId()
        + "was not fully completed as an exception was thrown: " + throwable.getMessage()
        + "Stack Trace: " + throwable.getStackTrace());
  }
}
