package unit;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.FileIO;
import com.gala.sam.tradeEngine.utils.OrderCSVParser;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorFactory;
import com.gala.sam.tradeEngine.utils.TradeCSVParser;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorFactory;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorState;
import com.gala.sam.tradeEngine.utils.orderValidators.OrderValidatorFactory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class AdvancedMarketTradeTests {

  private static final String relativeDirectoryOfTestFiles = "/src/test/resources/Market Test Cases/";
  private static final String absoluteDirectoryOfTestFiles = Paths
      .get(System.getProperty("user.dir"), relativeDirectoryOfTestFiles).toString();

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
    log.info(String.format("Running test %d", testNumber));
    final List<AbstractOrderRequest> orders = readOrders(phase, testNumber);

    ITradeRepository tradeRepository = RepositoryMockHelper.getEmptyRepository(ITradeRepository.class);
    IOrderRepository orderRepository = RepositoryMockHelper.getEmptyRepository(IOrderRepository.class);

    EnteredOrderGeneratorState concreteOrderGenerator = new EnteredOrderGeneratorState();
    OrderProcessorFactory orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(ITradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(IOrderRepository.class));

    EnteredOrderGeneratorFactory enteredOrderGeneratorFactory = new EnteredOrderGeneratorFactory(
        new EnteredOrderGeneratorState());

    OrderValidatorFactory orderValidatorFactory = new OrderValidatorFactory();

    MarketService marketService = new MarketService(tradeRepository, orderRepository,
        enteredOrderGeneratorFactory, orderProcessorFactory, orderValidatorFactory);
    orders.stream().forEach(marketService::enterOrder);

    final List<Trade> trades = marketService.getAllMatchedTrades();
    final List<Trade> tradesReference = readTrades(phase, testNumber);
    Assert.assertEquals(
        "#" + Integer.toString(testNumber) + " Trades should be the same as the reference",
        tradesReference, trades);
  }

  private List<Trade> readTrades(int phase, int testNumber) throws IOException {
    log.debug("Reading Orders from file");
    String filepath = getOutputFilePath(phase, testNumber);
    final List<String> inputText = FileIO.readTestFile(filepath);
    return TradeCSVParser.decodeCSV(inputText);
  }

  private List<AbstractOrderRequest> readOrders(int phase, int testNumber) throws IOException {
    log.debug("Reading Orders from file");
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
