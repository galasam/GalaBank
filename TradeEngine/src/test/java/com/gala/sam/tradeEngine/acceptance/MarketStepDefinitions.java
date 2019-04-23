package com.gala.sam.tradeEngine.acceptance;

import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MarketStepDefinitions {

  @Autowired
  StepDefinitionWorldState worldState;

  @When("order is entered in to market")
  public void orderIsEnteredInToMarket() {
    worldState.response = worldState.restEntryPoint.enterOrder(worldState.orderRequest);
  }

}
