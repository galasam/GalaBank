package com.gala.sam.tradeEngine.utils.enteredOrderGenerators;


import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.utils.exception.OrderTypeNotSupportedException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EnteredOrderGeneratorFactory {

  private final EnteredOrderGeneratorState enteredOrderGeneratorState;

  public IEnteredOrderGenerator getEnteredOrderGenerator(OrderType type)
      throws OrderTypeNotSupportedException {
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
        log.error("Order type {} is not supported so cannot create entered order generator", type);
        throw new OrderTypeNotSupportedException(type);
    }
  }

  public void reset() {
    enteredOrderGeneratorState.reset();
  }
}
