package com.gala.sam.tradeEngine.utils.enteredOrderGenerators;

import com.gala.sam.tradeEngine.domain.orderrequest.StopLimitOrderRequest;
import com.gala.sam.tradeEngine.domain.enteredorder.StopLimitOrder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StopLimitOrderGenerator implements
    IEnteredOrderGenerator<StopLimitOrderRequest, StopLimitOrder> {

  final EnteredOrderGeneratorState enteredOrderGeneratorState;

  @Override
  public StopLimitOrder generateConcreteOrder(StopLimitOrderRequest orderRequest) {
    return StopLimitOrder.builder()
        .orderId(enteredOrderGeneratorState.getNextOrderId())
        .clientId(orderRequest.getClientId())
        .direction(orderRequest.getDirection())
        .quantity(orderRequest.getQuantity())
        .ticker(orderRequest.getTicker())
        .timeInForce(orderRequest.getTimeInForce())
        .limit(orderRequest.getLimit())
        .triggerPrice(orderRequest.getTriggerPrice())
        .build();
  }
}