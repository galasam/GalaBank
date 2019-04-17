package com.gala.sam.tradeEngine.acceptance;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.domain.orderrequest.LimitOrderRequest;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderRequestStepDefinitions {

  @Autowired
  StepDefinitionWorldState worldState;

  @Given("limit order with negative client id")
  public void limitOrderWithNegativeClientId() {
    worldState.orderRequest = LimitOrderRequest.builder()
        .direction(AbstractOrderRequest.Direction.BUY)
        .clientId(-1)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(AbstractOrderRequest.TimeInForce.GTC)
        .build();
  }

}
