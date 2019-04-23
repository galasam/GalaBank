package com.gala.sam.orderRequestLibrary.orderrequest;

import lombok.Builder;

public class StopMarketOrderRequest extends AbstractStopOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.STOP_MARKET;

  @Builder
  public StopMarketOrderRequest(int clientId, Direction direction, int quantity,
      TimeInForce timeInForce,
      String ticker, float triggerPrice) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker, triggerPrice);
  }

}
