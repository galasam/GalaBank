package com.gala.sam.tradeengine.utils.orderprocessors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeengine.UnitTest;
import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.domain.datastructures.LimitOrderQueue;
import com.gala.sam.tradeengine.domain.datastructures.LimitOrderQueue.SortingMethod;
import com.gala.sam.tradeengine.domain.datastructures.OrderIdPriorityQueue;
import com.gala.sam.tradeengine.domain.datastructures.TickerData;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeengine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.utils.MarketUtils;
import com.gala.sam.tradeengine.utils.orderprocessors.ActiveMarketOrderProcessor;
import com.gala.sam.tradeengine.utils.orderprocessors.OrderProcessorUtils;
import java.util.List;
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
@SuppressWarnings("unchecked")
public class MarketOrderProcessorTests {

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
    public ActiveMarketOrderProcessor activeMarketOrderProcessor() {
      return new ActiveMarketOrderProcessor(marketUtils, orderRepository, tradeRepository,
          orderProcessorUtils);
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
  private ActiveMarketOrderProcessor activeMarketOrderProcessor;

  @Test
  public void whenLimitQueueIsEmpty() {
    //Given limit queue that is empty
    MarketOrder marketOrder = MarketOrder.builder().build();

    List<Trade> trades = mock(List.class);
    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> limitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeMarketOrderProcessor
        .processDirectedMarketOrder(trades, marketOrder, tickerData, limitOrders, marketOrders);

    //Then: queueIfTimeInForce is called with the right parameters
    verify(marketUtils).queueIfGTC(eq(marketOrder), eq(marketOrders), any());
    verify(marketUtils, never()).tryMakeTrade(any(), any(), any(), anyFloat(), any());

  }

  @Test
  public void whenLimitQueueIsNotEmpty() {
    //Given limit queue with a limit order in it
    MarketOrder marketOrder = MarketOrder.builder().build();
    LimitOrder limitOrderBest = LimitOrder.builder().limit(10).build();
    LimitOrder limitOrderWorst = LimitOrder.builder().limit(20).build();

    List<Trade> trades = mock(List.class);
    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> limitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    limitOrders.add(limitOrderWorst);
    limitOrders.add(limitOrderBest);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeMarketOrderProcessor
        .processDirectedMarketOrder(trades, marketOrder, tickerData, limitOrders, marketOrders);

    //Then: queueIfTimeInForce is not called and a trade is made
    verify(marketUtils, never()).queueIfGTC(any(), any(), any());
    verify(marketUtils, never()).queueIfGTC(any(), any(), any());
    verify(orderProcessorUtils).continueProcessingMarketOrderIfNotFulfilled(eq(trades),
        eq(marketOrder), eq(tickerData), eq(limitOrders), eq(marketOrders),
        eq(activeMarketOrderProcessor));
    verify(orderProcessorUtils).removeOrderIfFulfilled(any(), any(), any());
  }
}
