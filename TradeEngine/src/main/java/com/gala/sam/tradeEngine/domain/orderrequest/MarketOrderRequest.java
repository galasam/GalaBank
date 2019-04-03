package com.gala.sam.tradeEngine.domain.orderrequest;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import lombok.Builder;

public class MarketOrderRequest extends AbstractActiveOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.ACTIVE_MARKET;

  @Builder
  public MarketOrderRequest(int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker);
  }

  @Override
  public AbstractOrder toConcrete(int orderId) {
    return com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder.builder()
        .orderId(orderId)
        .clientId(getClientId())
        .direction(getDirection())
        .quantity(getQuantity())
        .ticker(getTicker())
        .timeInForce(getTimeInForce())
        .build();
  }

}
