package com.gala.sam.tradeEngine.domain.OrderRequest;

import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import lombok.Builder;
import lombok.Value;

@Value
public class StopMarketOrderRequest extends StopOrderRequest {

  @Builder
  public StopMarketOrderRequest(int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce,
      String ticker, float triggerPrice) {
    super(clientId, direction, quantity, timeInForce, ticker, triggerPrice);
  }

  @Override
  public Order toConcrete(int orderId) {
    return com.gala.sam.tradeEngine.domain.EnteredOrder.StopMarketOrder.builder()
        .orderId(orderId)
        .clientId(getClientId())
        .direction(getDirection())
        .quantity(getQuantity())
        .ticker(getTicker())
        .timeInForce(getTimeInForce())
        .triggerPrice(getTriggerPrice())
        .build();
  }
}
