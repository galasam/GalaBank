package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.utils.exception.OrderTypeNotSupportedException;
import org.springframework.stereotype.Component;

@Component
public class OrderValidatorFactory {

  public IOrderValidator getOrderValidator(OrderType type) throws OrderTypeNotSupportedException {
    switch(type) {
      case ACTIVE_LIMIT:
        return new ActiveLimitOrderValidator();
      case ACTIVE_MARKET:
        return new ActiveMarketOrderValidator();
      case STOP_LIMIT:
        return new StopLimitOrderValidator();
      case STOP_MARKET:
        return new StopMarketOrderValidator();
      default:
        throw new OrderTypeNotSupportedException(type);
    }
  }

}
