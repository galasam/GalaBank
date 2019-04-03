package com.gala.sam.tradeEngine.domain.OrderRequest;

import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StopLimitOrderRequest extends StopOrderRequest {

  float limit;

  @Builder
  public StopLimitOrderRequest(int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce,
      String ticker, float triggerPrice, float limit) {
    super(clientId, direction, quantity, timeInForce, ticker, triggerPrice);
    this.limit = limit;
  }

  @Override
  public Order toConcrete(int orderId) {
    return com.gala.sam.tradeEngine.domain.EnteredOrder.StopLimitOrder.builder()
        .orderId(orderId)
        .clientId(getClientId())
        .direction(getDirection())
        .quantity(getQuantity())
        .ticker(getTicker())
        .timeInForce(getTimeInForce())
        .limit(getLimit())
        .triggerPrice(getTriggerPrice())
        .build();
  }

}
