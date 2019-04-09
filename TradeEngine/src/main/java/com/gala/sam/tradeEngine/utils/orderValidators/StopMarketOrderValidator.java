package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.StopMarketOrderRequest;
import com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers.BaseOrderValidatorHelper;
import com.gala.sam.tradeEngine.utils.orderValidators.validatorHelpers.StopOrderValidatorHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopMarketOrderValidator implements IOrderValidator<StopMarketOrderRequest> {

  public List<String> findErrors(StopMarketOrderRequest order) {
    List<String> errors = BaseOrderValidatorHelper.findErrors(order);
    errors.addAll(StopOrderValidatorHelper.findErrors(order));
    if (errors.size() > 0) {
      log.debug("StopLimitOrderValidator has found {} errors on order {} : {}",
          errors.size(), order, errors);
    }
    return errors;
  }
}
