package com.gala.sam.tradeEngine.acceptance;

import com.gala.sam.tradeEngine.domain.OrderRequestResponse;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.entrypoint.RestEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StepDefinitionWorldState {

  @Autowired
  RestEntryPoint restEntryPoint;

  AbstractOrderRequest orderRequest;
  OrderRequestResponse response;

}
