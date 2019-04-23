package com.gala.sam.tradeengine.utils.ordervalidators.validatorhelpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LimitOrderValidatorHelper {

  public static Collection<String> findErrorsInLimitPrice(float limit) {
    List<String> errors = new ArrayList<>();
    if (limit < 0) {
      errors.add("Limit price is negative");
    }
    return errors;
  }

}
