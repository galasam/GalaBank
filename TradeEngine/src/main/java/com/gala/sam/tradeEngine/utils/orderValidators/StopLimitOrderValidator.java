package com.gala.sam.tradeEngine.utils.orderValidators;

import static com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers.LimitOrderValidatorHelper.findErrorsInLimitPrice;

import com.gala.sam.orderRequestLibrary.orderrequest.StopLimitOrderRequest;
import com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers.BaseOrderValidatorHelper;
import com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers.StopOrderValidatorHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopLimitOrderValidator implements IOrderValidator<StopLimitOrderRequest> {

  public List<String> findErrors(StopLimitOrderRequest order) {
    List<String> errors = BaseOrderValidatorHelper.findErrors(order);
    errors.addAll(StopOrderValidatorHelper.findErrors(order));
    errors.addAll(findErrorsInLimitPrice(order.getLimit()));
    if (errors.size() > 0) {
      log.debug("StopLimitOrderValidator has found {} errors on order {} : {}",
          errors.size(), order, errors);
    }
    return errors;
  }

}
