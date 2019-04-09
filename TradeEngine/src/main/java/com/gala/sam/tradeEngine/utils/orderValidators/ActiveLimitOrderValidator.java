package com.gala.sam.tradeEngine.utils.orderValidators;

import static com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers.LimitOrderValidatorHelper.findErrorsInLimitPrice;

import com.gala.sam.tradeEngine.domain.orderrequest.LimitOrderRequest;
import com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers.BaseOrderValidatorHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveLimitOrderValidator implements IOrderValidator<LimitOrderRequest> {

  public List<String> findErrors(LimitOrderRequest order) {
    List<String> errors = BaseOrderValidatorHelper.findErrors(order);
    errors.addAll(findErrorsInLimitPrice(order.getLimit()));
    if (errors.size() > 0) {
      log.debug("ActiveLimitOrderValidator has found {} errors on order {} : {}",
          errors.size(), order, errors);
    }
    return errors;
  }


}
