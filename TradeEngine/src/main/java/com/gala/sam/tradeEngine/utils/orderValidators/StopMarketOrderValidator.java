package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.StopMarketOrderRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopMarketOrderValidator implements OrderValidator<StopMarketOrderRequest> {

  public List<String> findErrors(StopMarketOrderRequest order) {
    List<String> errors = BaseOrderValidator.findErrors(order);
    log.debug("StopLimitOrderValidator has found {} errors on order {} : {}",
        errors.size(), order, errors);
    return errors;
  }
}
