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
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.AbstractOrderFieldNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderTimeInForceNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.ProcessingActiveOrderException;
import com.gala.sam.tradeEngine.utils.orderProcessors.ActiveLimitOrderProcessor;
import java.util.SortedSet;
import org.junit.Test;

public class LimitOrderProcessorTests {

  @Test
  public void whenAllQueuesAreEmptyTest()
      throws OrderTimeInForceNotSupportedException, ProcessingActiveOrderException {
    //Given a limit order and empty queues
    LimitOrder limitOrder = LimitOrder.builder().build();

    IOrderRepository orderRepository = RepositoryMockHelper
        .getEmptyRepository(IOrderRepository.class);
    ITradeRepository ITradeRepository = RepositoryMockHelper
        .getEmptyRepository(ITradeRepository.class);
    MarketState marketState = mock(MarketState.class);
    MarketUtils marketUtils = mock(MarketUtils.class);

    ActiveLimitOrderProcessor activeLimitOrderProcessor = new ActiveLimitOrderProcessor(
        orderRepository, ITradeRepository, marketState, marketUtils);

    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeLimitOrderProcessor
        .processDirectedLimitOrder(limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
            oppositeTypeLimitOrders);

    //Then: queueIfTimeInForce is called with the right parameters
    verify(marketUtils).queueIfTimeInForce(eq(limitOrder), eq(sameTypeLimitOrders), any());
  }

  @Test
  public void whenMarketQueueEmptyNonMatchingLimitOrderInLimitQueue()
      throws ProcessingActiveOrderException, AbstractOrderFieldNotSupportedException {
    //Given: empty market order queue and a non matching limit order in limit order queue
    LimitOrder limitOrder = LimitOrder.builder().direction(Direction.BUY).limit(10.0f).build();
    LimitOrder nonMatchingLimitOrder = LimitOrder.builder().direction(Direction.SELL).limit(20.0f)
        .build();

    IOrderRepository orderRepository = RepositoryMockHelper
        .getEmptyRepository(IOrderRepository.class);
    ITradeRepository ITradeRepository = RepositoryMockHelper
        .getEmptyRepository(ITradeRepository.class);
    MarketState marketState = mock(MarketState.class);
    MarketUtils marketUtils = mock(MarketUtils.class);

    ActiveLimitOrderProcessor activeLimitOrderProcessor = new ActiveLimitOrderProcessor(
        orderRepository, ITradeRepository, marketState, marketUtils);

    SortedSet<MarketOrder> marketOrders = new OrderIdPriorityQueue<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    sameTypeLimitOrders.add(nonMatchingLimitOrder);
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeLimitOrderProcessor
        .processDirectedLimitOrder(limitOrder, tickerData, marketOrders, sameTypeLimitOrders,
            oppositeTypeLimitOrders);

    //Then: no trade is made and queueIfTimeInForce is called with the right parameters
    verify(marketUtils).queueIfTimeInForce(eq(limitOrder), eq(sameTypeLimitOrders), any());
    verify(marketUtils, never()).makeTrade(any(), any(), any(), anyFloat(), any());
  }
}
