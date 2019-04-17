package com.gala.sam.tradeEngine.acceptance;

import com.gala.sam.tradeEngine.domain.OrderRequestResponse;
import cucumber.api.java.en.Then;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

public class ResponseStepDefinitions {

  @Autowired
  StepDefinitionWorldState worldState;

  @Then("order is rejected")
  public void orderIsRejected() {
    Assert.assertEquals("Response is an error", OrderRequestResponse.ResponseType.ERROR,
        worldState.response.getResponseType());
  }

}
