package com.gala.sam.orderrequestlibrary.orderrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StopLimitOrderRequest extends AbstractStopOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.STOP_LIMIT;

  private final float limit;

  @Builder
  public StopLimitOrderRequest(int clientId, Direction direction, int quantity,
      TimeInForce timeInForce,
      String ticker, float triggerPrice, float limit) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker, triggerPrice);
    this.limit = limit;
  }

}
