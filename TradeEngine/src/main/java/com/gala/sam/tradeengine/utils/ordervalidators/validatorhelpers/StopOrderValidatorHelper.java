package com.gala.sam.tradeengine.utils.ordervalidators.validatorhelpers;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractStopOrderRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
