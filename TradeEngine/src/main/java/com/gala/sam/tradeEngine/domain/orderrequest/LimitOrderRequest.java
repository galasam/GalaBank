package com.gala.sam.tradeEngine.domain.orderrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

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
