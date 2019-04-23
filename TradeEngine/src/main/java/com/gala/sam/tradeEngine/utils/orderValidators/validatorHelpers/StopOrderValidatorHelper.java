package com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers;

import com.gala.sam.orderRequestLibrary.orderrequest.AbstractStopOrderRequest;
import java.util.ArrayList;
import java.util.List;

public class StopOrderValidatorHelper {

  public static List<String> findErrors(AbstractStopOrderRequest order) {
    List<String> errors = new ArrayList<>();
    errors.addAll(findErrorsInTriggerPrice(order.getTriggerPrice()));
    return errors;
  }

  static private List<String> findErrorsInTriggerPrice(float triggerPrice) {
    List<String> errors = new ArrayList<>();
    if (triggerPrice < 0) {
      errors.add("Trigger price is negative");
    }
    return errors;
  }
}
