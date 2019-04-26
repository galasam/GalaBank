package com.gala.sam.tradeengine.utils.orderprocessors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeengine.UnitTest;
import com.gala.sam.tradeengine.domain.datastructures.LimitOrderQueue;
import com.gala.sam.tradeengine.domain.datastructures.LimitOrderQueue.SortingMethod;
import com.gala.sam.tradeengine.domain.datastructures.MarketState;
import com.gala.sam.tradeengine.domain.datastructures.OrderIdPriorityQueue;
import com.gala.sam.tradeengine.domain.datastructures.TickerData;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeengine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.utils.MarketUtils;
import com.gala.sam.tradeengine.utils.orderprocessors.ActiveLimitOrderProcessor.ActiveLimitOrderProcess;
import java.util.SortedSet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@Category(UnitTest.class)
@RunWith(SpringRunner.class)
public class LimitOrderProcessorTests {

  @MockBean
  IOrderRepository orderRepository;
  @MockBean
  ITradeRepository tradeRepository;
  @MockBean
  MarketUtils marketUtils;
  @MockBean
  OrderProcessorUtils orderProcessorUtils;
  @Autowired
  ActiveLimitOrderProcessor activeLimitOrderProcessor;

  @Test
  public void whenAllQueuesAreEmptyTest() {
    //Given a limit order and empty queues
    LimitOrder limitOrder = LimitOrder.builder().build();

    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    TickerData tickerData = mock(TickerData.class);
    MarketState marketState = mock(MarketState.class);
    given(marketState.getTickerQueueGroup(any())).willReturn(tickerData);

    //When: process is called
    ActiveLimitOrderProcess process = activeLimitOrderProcessor.new ActiveLimitOrderProcess(
        marketState, limitOrder);
    process.processDirectedLimitOrder(marketOrders, sameTypeLimitOrders, oppositeTypeLimitOrders);

    //Then: queueIfTimeInForce is called with the right parameters
    verify(marketUtils).queueIfGTC(eq(limitOrder), eq(sameTypeLimitOrders), any());
  }

  @Test
  public void whenMarketQueueEmptyNonMatchingLimitOrderInLimitQueue() {
    //Given: empty market order queue and a non matching limit order in limit order queue
    LimitOrder limitOrder = LimitOrder.builder().direction(Direction.BUY).limit(10.0f).build();
    LimitOrder nonMatchingLimitOrder = LimitOrder.builder().direction(Direction.SELL).limit(20.0f)
        .build();

    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    oppositeTypeLimitOrders.add(nonMatchingLimitOrder);
    TickerData tickerData = mock(TickerData.class);
    MarketState marketState = mock(MarketState.class);
    given(marketState.getTickerQueueGroup(any())).willReturn(tickerData);

    //When: process is called
    ActiveLimitOrderProcess process = activeLimitOrderProcessor.new ActiveLimitOrderProcess(
        marketState, limitOrder);
    process.processDirectedLimitOrder(marketOrders, sameTypeLimitOrders, oppositeTypeLimitOrders);

    //Then: no trade is made and queueIfGTC is called with the right parameters
    verify(marketUtils).queueIfGTC(eq(limitOrder), eq(sameTypeLimitOrders), any());
    verify(marketUtils, never()).tryMakeTrade(any(), any(), any(), anyFloat(), any());
  }

  @Test
  public void whenMarketQueueEmptyMatchingLimitOrderInLimitQueue() {
    //Given: empty market order queue and a matching limit order in limit order queue
    LimitOrder limitOrder = LimitOrder.builder().direction(Direction.BUY).limit(20.0f).build();
    LimitOrder matchingLimitOrderBest = LimitOrder.builder().direction(Direction.SELL).limit(10.0f)
        .build();
    LimitOrder matchingLimitOrderWorst = LimitOrder.builder().direction(Direction.SELL).limit(15.0f)
        .build();

    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    oppositeTypeLimitOrders.add(matchingLimitOrderWorst);
    oppositeTypeLimitOrders.add(matchingLimitOrderBest);
    TickerData tickerData = mock(TickerData.class);
    MarketState marketState = mock(MarketState.class);
    given(marketState.getTickerQueueGroup(any())).willReturn(tickerData);

    //When: process is called
    ActiveLimitOrderProcess process = activeLimitOrderProcessor.new ActiveLimitOrderProcess(
        marketState, limitOrder);
    process.processDirectedLimitOrder(marketOrders, sameTypeLimitOrders, oppositeTypeLimitOrders);

    //Then: a trade is made with right params and queueIfGTC is not called
    verify(marketUtils)
        .tryMakeTrade(any(), eq(limitOrder), eq(matchingLimitOrderBest),
            eq(matchingLimitOrderBest.getLimit()),
            eq(tickerData));
    verify(marketUtils, never()).queueIfGTC(any(), any(), any());
    verify(orderProcessorUtils)
        .continueProcessingLimitOrderIfNotFulfilled(eq(limitOrder), eq(process));
    verify(orderProcessorUtils).removeOrderIfFulfilled(any(), any(), any());
  }

  @Test
  public void whenMarketQueueNotEmpty() {
    //Given: non empty market order queue
    LimitOrder limitOrder = LimitOrder.builder().build();
    MarketOrder marketOrderFirst = MarketOrder.builder().orderId(1).build();
    MarketOrder marketOrderSecond = MarketOrder.builder().orderId(2).build();

    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    marketOrders.add(marketOrderFirst);
    marketOrders.add(marketOrderSecond);
    TickerData tickerData = mock(TickerData.class);
    MarketState marketState = mock(MarketState.class);
    given(marketState.getTickerQueueGroup(any())).willReturn(tickerData);

    //When: process is called
    ActiveLimitOrderProcess process = activeLimitOrderProcessor.new ActiveLimitOrderProcess(
        marketState, limitOrder);
    process.processDirectedLimitOrder(marketOrders, sameTypeLimitOrders, oppositeTypeLimitOrders);

    //Then: a trade is made with right params and queueIfGTC is not called
    verify(marketUtils)
        .tryMakeTrade(any(), eq(limitOrder), eq(marketOrderFirst), eq(limitOrder.getLimit()),
            eq(tickerData));
    verify(marketUtils, never()).queueIfGTC(any(), any(), any());
    verify(orderProcessorUtils)
        .continueProcessingLimitOrderIfNotFulfilled(eq(limitOrder), eq(process));
    verify(orderProcessorUtils).removeOrderIfFulfilled(any(), any(), any());
  }

  @TestConfiguration
  static class Config {

    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    ITradeRepository tradeRepository;
    @Autowired
    MarketUtils marketUtils;
    @Autowired
    OrderProcessorUtils orderProcessorUtils;

    @Bean
    public ActiveLimitOrderProcessor activeLimitOrderProcessor() {
      return new ActiveLimitOrderProcessor(marketUtils, orderRepository, tradeRepository,
          orderProcessorUtils);
    }
  }

}
