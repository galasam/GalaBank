package com.gala.sam.tradeEngine.domain.orderrequest;


public abstract class StopOrderRequest extends OrderRequest {

  float triggerPrice;

  public StopOrderRequest(int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce,
      String ticker, float triggerPrice) {
    super(OrderType.STOP, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public float getTriggerPrice() {
    return triggerPrice;
  }

}
