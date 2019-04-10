package com.gala.sam.tradeEngine.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue.SortingMethod;
import com.gala.sam.tradeEngine.domain.datastructures.OrderIdPriorityQueue;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.orderProcessors.ActiveLimitOrderProcessor;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorUtils;
import com.gala.sam.tradeEngine.utils.orderProcessors.StopOrderProcessor;
import java.util.List;
import java.util.SortedSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class LimitOrderProcessorTests {

  @TestConfiguration
  static class Config {
    @Bean
    public ActiveLimitOrderProcessor activeLimitOrderProcessor() {
      return new ActiveLimitOrderProcessor();
    }
  }

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

    List<Trade> trades = mock(List.class);
    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeLimitOrderProcessor
        .processDirectedLimitOrder(trades, limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
            oppositeTypeLimitOrders);

    //Then: queueIfTimeInForce is called with the right parameters
    verify(marketUtils).queueIfGTC(eq(limitOrder), eq(sameTypeLimitOrders), any());
  }

  @Test
  public void whenMarketQueueEmptyNonMatchingLimitOrderInLimitQueue() {
    //Given: empty market order queue and a non matching limit order in limit order queue
    LimitOrder limitOrder = LimitOrder.builder().direction(Direction.BUY).limit(10.0f).build();
    LimitOrder nonMatchingLimitOrder = LimitOrder.builder().direction(Direction.SELL).limit(20.0f)
        .build();

    List<Trade> trades = mock(List.class);
    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    oppositeTypeLimitOrders.add(nonMatchingLimitOrder);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeLimitOrderProcessor
        .processDirectedLimitOrder(trades, limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
            oppositeTypeLimitOrders);

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

    List<Trade> trades = mock(List.class);
    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    oppositeTypeLimitOrders.add(matchingLimitOrderWorst);
    oppositeTypeLimitOrders.add(matchingLimitOrderBest);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeLimitOrderProcessor
        .processDirectedLimitOrder(trades, limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
            oppositeTypeLimitOrders);

    //Then: a trade is made with right params and queueIfGTC is not called
    verify(marketUtils)
        .tryMakeTrade(any(), eq(limitOrder), eq(matchingLimitOrderBest),
            eq(matchingLimitOrderBest.getLimit()),
            eq(tickerData));
    verify(marketUtils, never()).queueIfGTC(any(), any(), any());
    verify(orderProcessorUtils).continueProcessingLimitOrderIfNotFulfilled(eq(trades),
        eq(limitOrder), eq(tickerData), eq(marketOrders), eq(sameTypeLimitOrders),
        eq(oppositeTypeLimitOrders), eq(activeLimitOrderProcessor));
    verify(orderProcessorUtils).removeOrderIfFulfilled(any(), any(), any());
  }

  @Test
  public void whenMarketQueueNotEmpty() {
    //Given: non empty market order queue
    LimitOrder limitOrder = LimitOrder.builder().build();
    MarketOrder marketOrderFirst = MarketOrder.builder().orderId(1).build();
    MarketOrder marketOrderSecond = MarketOrder.builder().orderId(2).build();

    List<Trade> trades = mock(List.class);
    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    marketOrders.add(marketOrderFirst);
    marketOrders.add(marketOrderSecond);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeLimitOrderProcessor
        .processDirectedLimitOrder(trades, limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
            oppositeTypeLimitOrders);

    //Then: a trade is made with right params and queueIfGTC is not called
    verify(marketUtils)
        .tryMakeTrade(any(), eq(limitOrder), eq(marketOrderFirst), eq(limitOrder.getLimit()),
            eq(tickerData));
    verify(marketUtils, never()).queueIfGTC(any(), any(), any());
    verify(orderProcessorUtils).continueProcessingLimitOrderIfNotFulfilled(eq(trades),
        eq(limitOrder), eq(tickerData), eq(marketOrders), eq(sameTypeLimitOrders),
        eq(oppositeTypeLimitOrders), eq(activeLimitOrderProcessor));
    verify(orderProcessorUtils).removeOrderIfFulfilled(any(), any(), any());
  }

}
