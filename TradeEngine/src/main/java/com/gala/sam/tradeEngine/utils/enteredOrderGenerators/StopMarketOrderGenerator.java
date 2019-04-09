package com.gala.sam.tradeEngine.utils.enteredOrderGenerators;

import com.gala.sam.tradeEngine.domain.orderrequest.StopMarketOrderRequest;
import com.gala.sam.tradeEngine.domain.enteredorder.StopMarketOrder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StopMarketOrderGenerator implements
    IEnteredOrderGenerator<StopMarketOrderRequest, StopMarketOrder> {

  final EnteredOrderGeneratorState enteredOrderGeneratorState;

  @Override
  public StopMarketOrder generateConcreteOrder(StopMarketOrderRequest orderRequest) {
    return StopMarketOrder.builder()
        .orderId(enteredOrderGeneratorState.getNextOrderId())
        .clientId(orderRequest.getClientId())
        .direction(orderRequest.getDirection())
        .quantity(orderRequest.getQuantity())
        .ticker(orderRequest.getTicker())
        .timeInForce(orderRequest.getTimeInForce())
        .triggerPrice(orderRequest.getTriggerPrice())
        .build();
  }
}