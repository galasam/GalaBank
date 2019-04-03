package com.gala.sam.tradeEngine.domain.orderrequest;

public abstract class ActiveOrderRequest extends OrderRequest {

  public ActiveOrderRequest(OrderType type, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    super(type, clientId, direction, quantity, timeInForce, ticker);
  }

}
