package com.gala.sam.tradeEngine.acceptance;


import com.gala.sam.orderRequestLibrary.OrderRequestResponse;
import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest;
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
