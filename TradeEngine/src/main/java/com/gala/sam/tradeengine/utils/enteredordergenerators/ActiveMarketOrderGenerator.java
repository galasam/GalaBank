package com.gala.sam.tradeengine.utils.enteredordergenerators;

import com.gala.sam.tradeengine.domain.enteredorder.MarketOrder;
import com.gala.sam.orderrequestlibrary.orderrequest.MarketOrderRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ActiveMarketOrderGenerator implements
    IEnteredOrderGenerator<MarketOrderRequest, MarketOrder> {

  final EnteredOrderGeneratorState enteredOrderGeneratorState;

  @Override
  public MarketOrder generateConcreteOrder(MarketOrderRequest orderRequest) {
    return MarketOrder.builder()
        .orderId(enteredOrderGeneratorState.getNextOrderId())
        .clientId(orderRequest.getClientId())
        .direction(orderRequest.getDirection())
        .quantity(orderRequest.getQuantity())
        .ticker(orderRequest.getTicker())
        .timeInForce(orderRequest.getTimeInForce())
        .build();
  }
}