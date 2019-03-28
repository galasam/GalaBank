package com.gala.sam.tradeEngine.domain.OrderReq;

import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StopLimitOrderReq extends StopOrderReq {

  float limit;

  @Builder
  public StopLimitOrderReq(int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce,
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
