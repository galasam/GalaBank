package com.gala.sam.tradeengine.integration;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.MarketOrderRequest;
import com.gala.sam.tradeengine.IntegrationTest;
import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.service.MarketService;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
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
public class SimpleMarketTradeTests {

  @MockBean
  ITradeRepository tradeRepository;
  @MockBean
  IOrderRepository orderRepository;

  @Autowired
  MarketService marketService;

  @Before
  public void resetMarketService() {
    marketService.reset();
  }

  @Test
  public void testSimpleTimeStep() {
    //Given: Limit Buy and Market sell orders that should match
    LimitOrderRequest limitOrder = LimitOrderRequest.builder()
        .direction(Direction.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(TimeInForce.GTC)
        .build();

    MarketOrderRequest marketOrder = MarketOrderRequest.builder()
        .direction(Direction.SELL)
        .quantity(999)
        .ticker("Fred")
        .timeInForce(TimeInForce.GTC)
        .build();

    //When: They are entered in to the market
    int limitOrderId = marketService.enterOrder(limitOrder).get().getOrderId();
    int marketOrderId = marketService.enterOrder(marketOrder).get().getOrderId();

    //Then: a correct trade should be made
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

    //Give: two limit orders that should match
    LimitOrderRequest limitOrderA = LimitOrderRequest.builder()
        .direction(Direction.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(TimeInForce.GTC)
        .build();

    LimitOrderRequest limitOrderBMatchingA = LimitOrderRequest.builder()
        .direction(Direction.SELL)
        .quantity(999)
        .ticker("Fred")
        .limit(2f)
        .timeInForce(TimeInForce.GTC)
        .build();

    //When: they are entered in to the market
    int orderIdA = marketService.enterOrder(limitOrderA).get().getOrderId();
    int orderIdB = marketService.enterOrder(limitOrderBMatchingA).get().getOrderId();

    //Then: a correct trade should be produced
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

    //Given: two limit orders that have non-matching limits
    LimitOrderRequest limitOrderA = LimitOrderRequest.builder()
        .direction(Direction.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(TimeInForce.GTC)
        .build();

    LimitOrderRequest limitOrderBNotMatchingA = LimitOrderRequest.builder()
        .direction(Direction.SELL)
        .quantity(999)
        .ticker("Fred")
        .limit(10f)
        .timeInForce(TimeInForce.GTC)
        .build();

    //When: they are entered in to the market
    marketService.enterOrder(limitOrderA);
    marketService.enterOrder(limitOrderBNotMatchingA);

    //Then: No trade should be produced
    List<Trade> trades = marketService.getAllMatchedTrades();

    Assert.assertEquals("Should not match a buy and sell non-matching limit orders", 0,
        trades.size());
  }

  @Test
  public void testOrderPartialFulfillment() {

    //Given: 2 buy orders and a sell order with the quantity of them added together
    LimitOrderRequest limitOrderA = LimitOrderRequest.builder()
        .direction(Direction.BUY)
        .quantity(10)
        .ticker("Fred")
        .limit(4)
        .timeInForce(TimeInForce.GTC)
        .build();

    LimitOrderRequest limitOrderB = LimitOrderRequest.builder()
        .direction(Direction.BUY)
        .quantity(20)
        .ticker("Fred")
        .limit(3)
        .timeInForce(TimeInForce.GTC)
        .build();

    MarketOrderRequest marketOrder = MarketOrderRequest.builder()
        .direction(Direction.SELL)
        .quantity(30)
        .ticker("Fred")
        .timeInForce(TimeInForce.GTC)
        .build();

    //When: the orders are entered in to the market
    int limitOrderAId = marketService.enterOrder(limitOrderA).get().getOrderId();
    int limitOrderBId = marketService.enterOrder(limitOrderB).get().getOrderId();
    int marketOrderId = marketService.enterOrder(marketOrder).get().getOrderId();

    //Then: two trades should occur, with the sell order split over the two buy orders
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
    Assert.assertEquals("Should result in two trades", 2, results.size());
    Assert.assertEquals("First half of market order should match with limit order A", tradeA,
        results.get(0));
    Assert.assertEquals("Second half of market order should match with limit order B", tradeB,
        results.get(1));
  }

}
