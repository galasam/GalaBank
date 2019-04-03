package com.gala.sam.tradeEngine.domain.orderrequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StopLimitOrderRequest extends AbstractStopOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.STOP_LIMIT;

  float limit;

  @Builder
  public StopLimitOrderRequest(int clientId, Direction direction, int quantity,
      TimeInForce timeInForce,
      String ticker, float triggerPrice, float limit) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker, triggerPrice);
    this.limit = limit;
  }

}
