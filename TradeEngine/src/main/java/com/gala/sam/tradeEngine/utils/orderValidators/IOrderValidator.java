package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest;
import java.util.List;

public interface IOrderValidator<T extends AbstractOrderRequest> {

  List<String> findErrors(T order);
}
