package com.gala.sam.tradeengine.utils.ordervalidators;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import java.util.List;

public interface IOrderValidator<T extends AbstractOrderRequest> {

  List<String> findErrors(T order);
}
