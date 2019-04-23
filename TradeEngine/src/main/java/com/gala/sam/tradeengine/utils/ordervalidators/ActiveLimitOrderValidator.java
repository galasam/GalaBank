package com.gala.sam.tradeengine.utils.ordervalidators;

import static com.gala.sam.tradeengine.utils.ordervalidators.validatorhelpers.LimitOrderValidatorHelper.findErrorsInLimitPrice;

import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import com.gala.sam.tradeengine.utils.ordervalidators.validatorhelpers.BaseOrderValidatorHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveLimitOrderValidator implements IOrderValidator<LimitOrderRequest> {

  public List<String> findErrors(LimitOrderRequest order) {
    List<String> errors = BaseOrderValidatorHelper.findErrors(order);
    errors.addAll(findErrorsInLimitPrice(order.getLimit()));
    if (!errors.isEmpty()) {
      log.debug("ActiveLimitOrderValidator has found {} errors on order {} : {}",
          errors.size(), order, errors);
    }
    return errors;
  }


}
