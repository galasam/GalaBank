package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.utils.exception.OrderTypeNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderValidatorFactory {

  public IOrderValidator getOrderValidator(OrderType type) throws OrderTypeNotSupportedException {
    switch (type) {
      case ACTIVE_LIMIT:
        return new ActiveLimitOrderValidator();
      case ACTIVE_MARKET:
        return new ActiveMarketOrderValidator();
      case STOP_LIMIT:
        return new StopLimitOrderValidator();
      case STOP_MARKET:
        return new StopMarketOrderValidator();
      default:
        log.error("Order type {} is not supported so cannot create order validator", type);
        throw new OrderTypeNotSupportedException(type);
    }
  }

}
