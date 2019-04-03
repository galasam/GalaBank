package com.gala.sam.tradeEngine.domain.orderrequest;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import lombok.Builder;
import lombok.Value;

@Value
public class StopMarketOrderRequest extends StopOrderRequest {

  @Builder
  public StopMarketOrderRequest(int clientId, Direction direction, int quantity, TimeInForce timeInForce,
      String ticker, float triggerPrice) {
    super(clientId, direction, quantity, timeInForce, ticker, triggerPrice);
  }

  @Override
  public AbstractOrder toConcrete(int orderId) {
    return com.gala.sam.tradeEngine.domain.enteredorder.StopMarketOrder.builder()
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
