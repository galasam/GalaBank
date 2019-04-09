package unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue.SortingMethod;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.OrderIdPriorityQueue;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.orderProcessors.ActiveMarketOrderProcessor;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorUtils;
import helpers.MockHelper;
import java.util.SortedSet;
import org.junit.Test;

public class MarketOrderProcessorTests {

  @Test
  public void whenLimitQueueIsEmpty()
      throws OrderDirectionNotSupportedException {
    //Given limit queue that is empty
    MarketOrder marketOrder = MarketOrder.builder().build();

    IOrderRepository orderRepository = MockHelper
        .getEmptyRepository(IOrderRepository.class);
    ITradeRepository tradeRepository = MockHelper
        .getEmptyRepository(ITradeRepository.class);
    MarketState marketState = mock(MarketState.class);
    MarketUtils marketUtils = mock(MarketUtils.class);
    OrderProcessorUtils orderProcessorUtils = mock(OrderProcessorUtils.class);

    ActiveMarketOrderProcessor activeMarketOrderProcessor = new ActiveMarketOrderProcessor(
        orderRepository, tradeRepository, marketState, marketUtils, orderProcessorUtils);

    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> limitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeMarketOrderProcessor
        .processDirectedMarketOrder(marketOrder, tickerData, limitOrders, marketOrders);

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

    IOrderRepository orderRepository = MockHelper
        .getEmptyRepository(IOrderRepository.class);
    ITradeRepository tradeRepository = MockHelper
        .getEmptyRepository(ITradeRepository.class);
    MarketState marketState = mock(MarketState.class);
    MarketUtils marketUtils = mock(MarketUtils.class);
    OrderProcessorUtils orderProcessorUtils = mock(OrderProcessorUtils.class);

    ActiveMarketOrderProcessor activeMarketOrderProcessor = new ActiveMarketOrderProcessor(
        orderRepository, tradeRepository, marketState, marketUtils, orderProcessorUtils);

    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> limitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    limitOrders.add(limitOrderWorst);
    limitOrders.add(limitOrderBest);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeMarketOrderProcessor
        .processDirectedMarketOrder(marketOrder, tickerData, limitOrders, marketOrders);

    //Then: queueIfTimeInForce is not called and a trade is made
    verify(marketUtils, never()).queueIfGTC(any(), any(), any());
    verify(marketUtils, never()).queueIfGTC(any(), any(), any());
    verify(orderProcessorUtils).continueProcessingMarketOrderIfNotFulfilled(
        eq(marketOrder), eq(tickerData), eq(limitOrders), eq(marketOrders),
        eq(activeMarketOrderProcessor));
    verify(orderProcessorUtils).removeOrderIfFulfilled(any(), any(), any());
  }
}
