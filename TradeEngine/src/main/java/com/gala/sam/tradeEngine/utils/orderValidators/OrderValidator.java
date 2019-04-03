package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import java.util.List;

public interface OrderValidator<T extends AbstractOrderRequest> {
  List<String> findErrors(T order);
}
