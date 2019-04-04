package com.gala.sam.tradeEngine.domain.orderrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class LimitOrderRequest extends AbstractActiveOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.ACTIVE_LIMIT;

  float limit;

  @Builder
  public LimitOrderRequest(int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker, float limit) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker);
    this.limit = limit;
  }

}
