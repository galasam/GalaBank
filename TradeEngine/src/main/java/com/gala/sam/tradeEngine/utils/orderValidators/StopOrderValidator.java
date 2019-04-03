package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractStopOrderRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StopOrderValidator extends BaseOrderValidator {

  static List<String> findErrors(AbstractStopOrderRequest order) {
    List<String> errors = BaseOrderValidator.findErrors(order);
    errors.addAll(findErrorsInTriggerPrice(order.getTriggerPrice()));
    return errors;
  }

  static private Collection<String> findErrorsInTriggerPrice(float triggerPrice) {
    List<String> errors = new ArrayList<>();
    if(triggerPrice < 0) {
      errors.add("Trigger price cannot be negative");
    }
    return errors;
  }
}
