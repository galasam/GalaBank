package com.gala.sam.orderrequestlibrary.orderrequest;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractStopOrderRequest extends AbstractOrderRequest {

  private final float triggerPrice;

  public AbstractStopOrderRequest(OrderType orderType, int clientId, Direction direction,
      int quantity, TimeInForce timeInForce,
      String ticker, float triggerPrice) {
    super(orderType, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public float getTriggerPrice() {
    return triggerPrice;
  }

}
