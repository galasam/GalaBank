package com.gala.sam.orderrequestlibrary.orderrequest;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class LimitOrderRequest extends AbstractActiveOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.ACTIVE_LIMIT;

  private final float limit;

  @Builder
  public LimitOrderRequest(int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker, float limit) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker);
    this.limit = limit;
  }

}
