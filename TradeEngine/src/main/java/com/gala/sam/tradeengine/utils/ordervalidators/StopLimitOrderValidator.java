package com.gala.sam.tradeengine.utils.ordervalidators;

import static com.gala.sam.tradeengine.utils.ordervalidators.validatorhelpers.LimitOrderValidatorHelper.findErrorsInLimitPrice;

import com.gala.sam.orderrequestlibrary.orderrequest.StopLimitOrderRequest;
import com.gala.sam.tradeengine.utils.ordervalidators.validatorhelpers.BaseOrderValidatorHelper;
import com.gala.sam.tradeengine.utils.ordervalidators.validatorhelpers.StopOrderValidatorHelper;
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
