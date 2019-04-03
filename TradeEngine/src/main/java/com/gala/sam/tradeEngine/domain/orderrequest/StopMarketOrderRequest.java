package com.gala.sam.tradeEngine.domain.orderrequest;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import lombok.Builder;
import lombok.Value;

@Value
public class StopMarketOrderRequest extends AbstractStopOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.STOP_MARKET;

  @Builder
  public StopMarketOrderRequest(int clientId, Direction direction, int quantity, TimeInForce timeInForce,
      String ticker, float triggerPrice) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker, triggerPrice);
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
