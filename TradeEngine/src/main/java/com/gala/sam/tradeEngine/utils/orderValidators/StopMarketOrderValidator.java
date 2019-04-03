package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.MarketOrderRequest;
import java.util.List;

public class StopMarketOrderValidator implements OrderValidator<MarketOrderRequest> {

  public List<String> findErrors(MarketOrderRequest order) {
    List<String> errors = BaseOrderValidator.findErrors(order);
    return errors;
  }
}
