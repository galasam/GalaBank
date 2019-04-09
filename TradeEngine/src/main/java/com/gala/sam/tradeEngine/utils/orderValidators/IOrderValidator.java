package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import java.util.List;

public interface IOrderValidator<T extends AbstractOrderRequest> {
  List<String> findErrors(T order);
}
