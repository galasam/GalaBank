package com.gala.sam.tradeEngine.utils.enteredOrderGenerators;


import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EnteredOrderGeneratorFactory {

  private final EnteredOrderGeneratorState enteredOrderGeneratorState;

  public EnteredOrderGenerator getEnteredOrderGenerator(OrderType type) {
    switch (type) {
      case ACTIVE_LIMIT:
        return new ActiveLimitOrderGenerator(enteredOrderGeneratorState);
      case ACTIVE_MARKET:
        return new ActiveMarketOrderGenerator(enteredOrderGeneratorState);
      case STOP_LIMIT:
        return new StopLimitOrderGenerator(enteredOrderGeneratorState);
      case STOP_MARKET:
        return new StopMarketOrderGenerator(enteredOrderGeneratorState);
      default:
        throw new UnsupportedOperationException("Order Type Not supported");
    }
  }
}
