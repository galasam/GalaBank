package com.gala.sam.orderrequestlibrary.orderrequest;

import lombok.Builder;

public class MarketOrderRequest extends AbstractActiveOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.ACTIVE_MARKET;

  @Builder
  public MarketOrderRequest(int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker);
  }

}
