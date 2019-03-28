package com.gala.sam.tradeEngine.domain.OrderReq;

public abstract class ActiveOrderReq extends OrderReq {

  public ActiveOrderReq(OrderType type, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    super(type, clientId, direction, quantity, timeInForce, ticker);
  }

}
