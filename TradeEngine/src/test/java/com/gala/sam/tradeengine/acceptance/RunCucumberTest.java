package com.gala.sam.tradeengine.acceptance;

import com.gala.sam.tradeengine.UserAcceptanceTest;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features")
@Category(UserAcceptanceTest.class)
public class RunCucumberTest {

}
