package com.gala.sam.tradeengine.integration;

import com.gala.sam.orderrequestlibrary.OrderCSVParser;
import com.gala.sam.tradeengine.IntegrationTest;
import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.service.MarketService;
import com.gala.sam.tradeengine.utils.FileIO;
import com.gala.sam.tradeengine.utils.TradeCSVParser;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Category(IntegrationTest.class)
public class AdvancedMarketTradeTests {

  private static final String relativeDirectoryOfTestFiles = "/src/test/resources/Market Test Cases/";
  private static final String absoluteDirectoryOfTestFiles = Paths
      .get(System.getProperty("user.dir"), relativeDirectoryOfTestFiles).toString();

  @MockBean
  ITradeRepository tradeRepository;
  @MockBean
  IOrderRepository orderRepository;

  @Autowired
  MarketService marketService;


  @Test
  public void correctlyHandlesPhase1TestCases() throws IOException {
    runPhaseTests(1);
  }


  @Test
  public void correctlyHandlesPhase2TestCases() throws IOException {
    runPhaseTests(2);
  }

  @Test
  public void correctlyHandlesPhase3TestCases() throws IOException {
    runPhaseTests(3);
  }

  @Test
  public void correctlyHandlesPhase4TestCases() throws IOException {
    runPhaseTests(4);
  }


  private void runPhaseTests(int phase) throws IOException {
    int testNumber = 1;
    while (testFileExists(phase, testNumber)) {
      runTest(phase, testNumber++);
    }
  }

  private void runTest(int phase, int testNumber) throws IOException {
    //Given some input orders
    final List<AbstractOrderRequest> orders = readOrders(phase, testNumber);
    marketService.reset();

    //When: they are entered in to the market
    orders.stream().forEach(marketService::enterOrder);

    //Then: the produced trades should match the expected outputs
    final List<Trade> trades = marketService.getAllMatchedTrades();
    final List<Trade> tradesReference = readTrades(phase, testNumber);
    Assert.assertEquals(
        "#" + Integer.toString(testNumber) + " Trades should be the same as the reference",
        tradesReference, trades);
  }

  private List<Trade> readTrades(int phase, int testNumber) throws IOException {
    String filepath = getOutputFilePath(phase, testNumber);
    final List<String> inputText = FileIO.readTestFile(filepath);
    return TradeCSVParser.decodeCSV(inputText);
  }

  private List<AbstractOrderRequest> readOrders(int phase, int testNumber) throws IOException {
    String filepath = getInputFilePath(phase, testNumber);
    final List<String> inputText = FileIO.readTestFile(filepath);
    return OrderCSVParser.decodeCSV(inputText);
  }

  private boolean testFileExists(int phase, int testNumber) {
    String filepath = getInputFilePath(phase, testNumber);
    return FileIO.fileExists(filepath);
  }

  private String getInputFilePath(int phaseNumber, int testNumber) {
    final String filename = String.format("input.test%d.%d.csv", phaseNumber, testNumber);
    return Paths.get(absoluteDirectoryOfTestFiles, "Phase" + phaseNumber, filename).toString();
  }

  private String getOutputFilePath(int phaseNumber, int testNumber) {
    final String filename = String.format("output.test%d.%d.csv", phaseNumber, testNumber);
    return Paths.get(absoluteDirectoryOfTestFiles, "Phase" + phaseNumber, filename).toString();
  }
}
