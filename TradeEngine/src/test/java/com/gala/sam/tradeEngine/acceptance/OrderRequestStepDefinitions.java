package com.gala.sam.tradeEngine.acceptance;

import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderRequestLibrary.orderrequest.LimitOrderRequest;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderRequestStepDefinitions {

  @Autowired
  private
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

  @Given("a valid order")
  public void aValidOrder() {
    worldState.orderRequest = LimitOrderRequest.builder()
        .direction(AbstractOrderRequest.Direction.BUY)
        .clientId(1)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(AbstractOrderRequest.TimeInForce.GTC)
        .build();
  }
}
