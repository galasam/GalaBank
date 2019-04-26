package com.gala.sam.tradeengine.utils.enteredordergenerators;

import com.gala.sam.orderrequestlibrary.orderrequest.StopMarketOrderRequest;
import com.gala.sam.tradeengine.domain.enteredorder.StopMarketOrder;
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