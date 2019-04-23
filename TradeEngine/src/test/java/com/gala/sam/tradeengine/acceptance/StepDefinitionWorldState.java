package com.gala.sam.tradeengine.acceptance;


import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeengine.entrypoint.RestEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StepDefinitionWorldState {

  @Autowired
  RestEntryPoint restEntryPoint;

  AbstractOrderRequest orderRequest;
  OrderRequestResponse response;

}
