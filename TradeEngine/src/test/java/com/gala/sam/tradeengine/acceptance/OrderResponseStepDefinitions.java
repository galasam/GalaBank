package com.gala.sam.tradeengine.acceptance;

import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse.ResponseType;
import cucumber.api.java.en.Then;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderResponseStepDefinitions {

  @Autowired
  private
  StepDefinitionWorldState worldState;

  @Then("order is rejected")
  public void orderIsRejected() {
    Assert.assertEquals("Response is an error", OrderRequestResponse.ResponseType.ERROR,
        worldState.response.getResponseType());
  }

  @Then("order is successfully entered")
  public void orderIsSuccessfullyEntered() {
    Assert.assertEquals("Response is successful", ResponseType.SUCCESS,
        worldState.response.getResponseType());
  }
}
