package com.gala.sam.tradeEngine.domain.OrderRequest;

import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import lombok.Builder;

public class MarketOrderRequest extends ActiveOrderRequest {

  @Builder
  public MarketOrderRequest(int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    super(OrderType.ACTIVE_MARKET, clientId, direction, quantity, timeInForce, ticker);
  }

  @Override
  public Order toConcrete(int orderId) {
    return com.gala.sam.tradeEngine.domain.EnteredOrder.MarketOrder.builder()
        .orderId(orderId)
        .clientId(getClientId())
        .direction(getDirection())
        .quantity(getQuantity())
        .ticker(getTicker())
        .timeInForce(getTimeInForce())
        .build();
  }

}
