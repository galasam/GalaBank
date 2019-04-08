package com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LimitOrderValidatorHelper {

  public static Collection<String> findErrorsInLimitPrice(float limit) {
    List<String> errors = new ArrayList<>();
    if (limit < 0) {
      errors.add("Limit price is negative");
    }
    return errors;
  }

}
