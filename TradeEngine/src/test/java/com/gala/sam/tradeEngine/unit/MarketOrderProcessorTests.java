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
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.orderProcessors.ActiveLimitOrderProcessor;
import com.gala.sam.tradeEngine.utils.orderProcessors.ActiveMarketOrderProcessor;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorUtils;
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
public class MarketOrderProcessorTests {

  @TestConfiguration
  static class Config {
    @Bean
    public ActiveMarketOrderProcessor activeMarketOrderProcessor() {
      return new ActiveMarketOrderProcessor();
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
  ActiveMarketOrderProcessor activeMarketOrderProcessor;

  @Test
  public void whenLimitQueueIsEmpty()
      throws OrderDirectionNotSupportedException {
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
