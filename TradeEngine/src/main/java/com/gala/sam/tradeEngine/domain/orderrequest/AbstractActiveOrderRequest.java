package com.gala.sam.tradeEngine.domain.orderrequest;

public abstract class AbstractActiveOrderRequest extends AbstractOrderRequest {

  public AbstractActiveOrderRequest(OrderType type, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    super(type, clientId, direction, quantity, timeInForce, ticker);
  }

}
