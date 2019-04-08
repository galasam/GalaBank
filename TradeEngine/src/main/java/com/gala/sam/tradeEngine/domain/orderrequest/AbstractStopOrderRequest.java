package com.gala.sam.tradeEngine.domain.orderrequest;

import lombok.ToString;

@ToString(callSuper = true)
public abstract class AbstractStopOrderRequest extends AbstractOrderRequest {

  float triggerPrice;

  public AbstractStopOrderRequest(OrderType orderType, int clientId, Direction direction, int quantity, TimeInForce timeInForce,
      String ticker, float triggerPrice) {
    super(orderType, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public float getTriggerPrice() {
    return triggerPrice;
  }

}
