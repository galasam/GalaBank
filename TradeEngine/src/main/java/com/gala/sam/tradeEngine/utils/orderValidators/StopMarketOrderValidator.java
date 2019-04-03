package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.MarketOrderRequest;
import com.gala.sam.tradeEngine.domain.orderrequest.StopMarketOrderRequest;
import java.util.List;

public class StopMarketOrderValidator implements OrderValidator<StopMarketOrderRequest> {

  public List<String> findErrors(StopMarketOrderRequest order) {
    List<String> errors = BaseOrderValidator.findErrors(order);
    return errors;
  }
}
