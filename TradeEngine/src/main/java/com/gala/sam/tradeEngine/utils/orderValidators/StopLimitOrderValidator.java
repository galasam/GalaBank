package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.StopLimitOrderRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StopLimitOrderValidator implements OrderValidator<StopLimitOrderRequest> {

  public List<String> findErrors(StopLimitOrderRequest order) {
    List<String> errors = StopOrderValidator.findErrors(order);
    errors.addAll(findErrorsInLimitPrice(order.getLimit()));
    return errors;
  }

  static private Collection<String> findErrorsInLimitPrice(float limit) {
    List<String> errors = new ArrayList<>();
    if(limit < 0) {
      errors.add("Limit price cannot be negative");
    }
    return errors;
  }
}
