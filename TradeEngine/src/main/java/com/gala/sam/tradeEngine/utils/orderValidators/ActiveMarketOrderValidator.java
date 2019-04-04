package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.MarketOrderRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveMarketOrderValidator implements IOrderValidator<MarketOrderRequest> {

  public List<String> findErrors(MarketOrderRequest order) {
    List<String> errors = BaseOrderValidator.findErrors(order);
    log.debug("ActiveMarketOrderValidator has found {} errors on order {} : {}",
        errors.size(), order, errors);
    return errors;
  }
}
