package com.gala.sam.tradeEngine.domain.orderrequest;


public abstract class StopOrderRequest extends AbstractOrderRequest {

  float triggerPrice;

  public StopOrderRequest(int clientId, Direction direction, int quantity, TimeInForce timeInForce,
      String ticker, float triggerPrice) {
    super(OrderType.STOP, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public float getTriggerPrice() {
    return triggerPrice;
  }

}
