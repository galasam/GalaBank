package com.gala.sam.tradeengine.utils.enteredordergenerators;

import com.gala.sam.tradeengine.domain.enteredorder.AbstractOrder;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;

public interface IEnteredOrderGenerator<T extends AbstractOrderRequest, S extends AbstractOrder> {

  S generateConcreteOrder(T orderRequest);
}
