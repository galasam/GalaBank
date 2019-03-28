package com.gala.sam.tradeEngine.domain.OrderReq;


public abstract class StopOrderReq extends OrderReq {

  float triggerPrice;

  public StopOrderReq(int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce,
      String ticker, float triggerPrice) {
    super(OrderType.STOP, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public float getTriggerPrice() {
    return triggerPrice;
  }

}
