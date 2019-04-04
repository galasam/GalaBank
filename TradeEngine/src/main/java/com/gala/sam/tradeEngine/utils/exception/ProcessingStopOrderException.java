package com.gala.sam.tradeEngine.utils.exception;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractStopOrder;

public class ProcessingStopOrderException extends Exception {

  public ProcessingStopOrderException(AbstractStopOrder order, Throwable throwable) {
    super("stop order: " + order.getOrderId() + "threw: " + throwable.getStackTrace());
  }
}
