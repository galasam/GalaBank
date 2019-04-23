package com.gala.sam.tradeengine.utils.ordervalidators;

import com.gala.sam.orderrequestlibrary.orderrequest.MarketOrderRequest;
import com.gala.sam.tradeengine.utils.ordervalidators.validatorHelpers.BaseOrderValidatorHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveMarketOrderValidator implements IOrderValidator<MarketOrderRequest> {

  public List<String> findErrors(MarketOrderRequest order) {
    List<String> errors = BaseOrderValidatorHelper.findErrors(order);
    if (errors.size() > 0) {
      log.debug("ActiveMarketOrderValidator has found {} errors on order {} : {}",
          errors.size(), order, errors);
    }
    return errors;
  }
}
