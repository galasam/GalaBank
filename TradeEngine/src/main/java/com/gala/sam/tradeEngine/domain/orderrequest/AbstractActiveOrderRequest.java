package com.gala.sam.tradeEngine.domain.orderrequest;

public abstract class AbstractActiveOrderRequest extends AbstractOrderRequest {

  public AbstractActiveOrderRequest(OrderType type, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker) {
    super(type, clientId, direction, quantity, timeInForce, ticker);
  }

}
