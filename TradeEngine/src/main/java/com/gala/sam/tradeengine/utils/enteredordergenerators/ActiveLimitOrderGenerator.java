package com.gala.sam.tradeengine.utils.enteredordergenerators;

import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ActiveLimitOrderGenerator implements
    IEnteredOrderGenerator<LimitOrderRequest, LimitOrder> {

  final EnteredOrderGeneratorState enteredOrderGeneratorState;

  @Override
  public LimitOrder generateConcreteOrder(LimitOrderRequest orderRequest) {
    return LimitOrder.builder()
        .orderId(enteredOrderGeneratorState.getNextOrderId())
        .clientId(orderRequest.getClientId())
        .direction(orderRequest.getDirection())
        .quantity(orderRequest.getQuantity())
        .ticker(orderRequest.getTicker())
        .timeInForce(orderRequest.getTimeInForce())
        .limit(orderRequest.getLimit())
        .build();
  }
}
