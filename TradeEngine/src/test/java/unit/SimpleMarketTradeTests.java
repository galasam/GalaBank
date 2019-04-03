package unit;

import com.gala.sam.tradeEngine.domain.OrderRequest.LimitOrderRequest;
import com.gala.sam.tradeEngine.domain.OrderRequest.MarketOrderRequest;
import com.gala.sam.tradeEngine.domain.OrderRequest.OrderRequest;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.ConcreteOrderGenerator;
import com.gala.sam.tradeEngine.utils.OrderProcessor.OrderProcessorFactory;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class SimpleMarketTradeTests {

  @Test
  public void testSimpleTimeStep() {

    OrderProcessorFactory orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class));
    MarketService marketService = new MarketService(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class),
        new ConcreteOrderGenerator(),
        orderProcessorFactory);

    LimitOrderRequest limitOrder = LimitOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    MarketOrderRequest marketOrder = MarketOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.SELL)
        .quantity(999)
        .ticker("Fred")
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    int limitOrderId = marketService.enterOrder(limitOrder).getOrderId();
    int marketOrderId = marketService.enterOrder(marketOrder).getOrderId();

    List<Trade> trades = marketService.getAllMatchedTrades();

    Trade tradeOutputTest = Trade.builder()
        .buyOrder(limitOrderId)
        .sellOrder(marketOrderId)
        .matchQuantity(marketOrder.getQuantity())
        .matchPrice(limitOrder.getLimit())
        .ticker(limitOrder.getTicker())
        .build();

    Assert.assertEquals("Should be able to match a buy limit and sell market order", 1,
        trades.size());
    Assert.assertEquals("Should match should be correct", trades.get(0), tradeOutputTest);
  }

  @Test
  public void testTimeStepWithMatchingLimits() {

    OrderProcessorFactory orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class));
    MarketService marketService = new MarketService(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class),
        new ConcreteOrderGenerator(),
        orderProcessorFactory);

    LimitOrderRequest limitOrderA = LimitOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    LimitOrderRequest limitOrderBMatchingA = LimitOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.SELL)
        .quantity(999)
        .ticker("Fred")
        .limit(2f)
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    int orderIdA = marketService.enterOrder(limitOrderA).getOrderId();
    int orderIdB = marketService.enterOrder(limitOrderBMatchingA).getOrderId();

    List<Trade> trades = marketService.getAllMatchedTrades();

    Trade tradeOutputTest = Trade.builder()
        .buyOrder(orderIdA)
        .sellOrder(orderIdB)
        .matchQuantity(limitOrderA.getQuantity())
        .matchPrice(limitOrderA.getLimit())
        .ticker(limitOrderA.getTicker())
        .build();

    Assert.assertEquals("Should be able to match a buy and sell matching limit orders", 1,
        trades.size());
    Assert.assertEquals("Should match should be correct", trades.get(0), tradeOutputTest);
  }

  @Test
  public void testTimeStepWithNonMatchingLimits() {

    OrderProcessorFactory orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class));
    MarketService marketService = new MarketService(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class),
        new ConcreteOrderGenerator(),
        orderProcessorFactory);

    LimitOrderRequest limitOrderA = LimitOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    LimitOrderRequest limitOrderBNotMatchingA = LimitOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.SELL)
        .quantity(999)
        .ticker("Fred")
        .limit(10f)
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    marketService.enterOrder(limitOrderA);
    marketService.enterOrder(limitOrderBNotMatchingA);
    List<Trade> trades = marketService.getAllMatchedTrades();

    Assert.assertEquals("Should not match a buy and sell non-matching limit orders", 0,
        trades.size());
  }

  @Test
  public void testOrderPartialFulfillment() {

    OrderProcessorFactory orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class));
    MarketService marketService = new MarketService(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class),
        new ConcreteOrderGenerator(),
        orderProcessorFactory);

    LimitOrderRequest limitOrderA = LimitOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.BUY)
        .quantity(10)
        .ticker("Fred")
        .limit(4)
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    LimitOrderRequest limitOrderB = LimitOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.BUY)
        .quantity(20)
        .ticker("Fred")
        .limit(3)
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    MarketOrderRequest marketOrder = MarketOrderRequest.builder()
        .direction(OrderRequest.DIRECTION.SELL)
        .quantity(30)
        .ticker("Fred")
        .timeInForce(OrderRequest.TIME_IN_FORCE.GTC)
        .build();

    int limitOrderAId = marketService.enterOrder(limitOrderA).getOrderId();
    int limitOrderBId = marketService.enterOrder(limitOrderB).getOrderId();
    int marketOrderId = marketService.enterOrder(marketOrder).getOrderId();

    Trade tradeA = Trade.builder()
        .buyOrder(limitOrderAId)
        .sellOrder(marketOrderId)
        .matchPrice(limitOrderA.getLimit())
        .matchQuantity(limitOrderA.getQuantity())
        .ticker(limitOrderA.getTicker())
        .build();

    Trade tradeB = Trade.builder()
        .buyOrder(limitOrderBId)
        .sellOrder(marketOrderId)
        .matchPrice(limitOrderB.getLimit())
        .matchQuantity(limitOrderB.getQuantity())
        .ticker(limitOrderB.getTicker())
        .build();

    List<Trade> results = marketService.getAllMatchedTrades();
    Assert.assertEquals("Should result in two trades", results.size(), 2);
    Assert.assertEquals("First half of market order should match with limit order A", tradeA,
        results.get(0));
    Assert.assertEquals("Second half of market order should match with limit order B", tradeB,
        results.get(1));
  }

}
