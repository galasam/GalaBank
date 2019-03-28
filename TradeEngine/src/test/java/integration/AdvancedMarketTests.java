package integration;

import com.gala.sam.tradeEngine.domain.OrderReq.Order;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.ConcreteOrderGenerator;
import com.gala.sam.tradeEngine.utils.FileIO;
import com.gala.sam.tradeEngine.utils.OrderCSVParser;
import com.gala.sam.tradeEngine.utils.OrderProcessor.OrderProcessorFactory;
import com.gala.sam.tradeEngine.utils.TradeCSVParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.repository.CrudRepository;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AdvancedMarketTests {

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
    while(testFileExists(phase, testNumber)) {
      runTest(phase, testNumber++);
    }
  }

  private void runTest(int phase, int testNumber) throws IOException {
    log.info(String.format("Running test %d", testNumber));
    final List<Order> orders = readOrders(phase, testNumber);

    TradeRepository tradeRepository = getEmptyRepository(TradeRepository.class);
    OrderRepository orderRepository = getEmptyRepository(OrderRepository.class);

    ConcreteOrderGenerator concreteOrderGenerator = new ConcreteOrderGenerator();
    OrderProcessorFactory orderProcessorFactory = new OrderProcessorFactory(getEmptyRepository(TradeRepository.class), getEmptyRepository(OrderRepository.class));

    MarketService marketService = new MarketService(tradeRepository, orderRepository, concreteOrderGenerator, orderProcessorFactory);
    orders.stream().forEach(marketService::enterOrder);

    final List<Trade> trades = marketService.getAllMatchedTrades();
    final List<Trade> tradesReference = readTrades(phase, testNumber);
    Assert.assertEquals("#" + Integer.toString(testNumber) + " Trades should be the same as the reference",
            tradesReference, trades);
  }

  private <T extends CrudRepository> T getEmptyRepository(Class<T> type) {
    T repository = mock(type);
    when(repository.findAll()).thenReturn(new ArrayList<>());
    return repository;
  }

  private List<Trade> readTrades(int phase, int testNumber) throws IOException {
    log.debug("Reading Orders from file");
    String filepath = getOutputFilePath(phase, testNumber);
    final List<String> inputText = FileIO.readTestFile(filepath);
    return TradeCSVParser.decodeCSV(inputText);
  }

  private List<Order> readOrders(int phase, int testNumber) throws IOException {
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
