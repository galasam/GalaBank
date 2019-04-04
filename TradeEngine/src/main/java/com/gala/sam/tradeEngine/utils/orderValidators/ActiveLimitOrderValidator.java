package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.LimitOrderRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveLimitOrderValidator implements OrderValidator<LimitOrderRequest> {

  public List<String> findErrors(LimitOrderRequest order) {
    List<String> errors = BaseOrderValidator.findErrors(order);
    errors.addAll(findErrorsInLimitPrice(order.getLimit()));
    log.debug("ActiveLimitOrderValidator has found {} errors on order {} : {}",
        errors.size(), order, errors);
    return errors;
  }

  static private Collection<String> findErrorsInLimitPrice(float limit) {
    List<String> errors = new ArrayList<>();
    if (limit < 0) {
      errors.add("Limit price cannot be negative");
    }
    return errors;
  }
}
