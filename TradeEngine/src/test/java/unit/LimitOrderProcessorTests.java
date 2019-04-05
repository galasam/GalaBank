package unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderTimeInForceNotSupportedException;
import com.gala.sam.tradeEngine.utils.orderProcessors.ActiveLimitOrderProcessor;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;

public class LimitOrderProcessorTests {

  @Test
  public void whenAllQueuesAreEmptyTest()
      throws OrderTimeInForceNotSupportedException, OrderDirectionNotSupportedException {
    //Given a limit order and empty queues
    LimitOrder limitOrder = LimitOrder.builder().build();

    IOrderRepository orderRepository = RepositoryMockHelper.getEmptyRepository(IOrderRepository.class);
    ITradeRepository ITradeRepository = RepositoryMockHelper.getEmptyRepository(ITradeRepository.class);
    MarketState marketState = mock(MarketState.class);
    MarketUtils marketUtils = mock(MarketUtils.class);

    ActiveLimitOrderProcessor activeLimitOrderProcessor = new ActiveLimitOrderProcessor(
        orderRepository, ITradeRepository, marketState, marketUtils);

    SortedSet<MarketOrder> marketOrders = new TreeSet<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new TreeSet<>();
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new TreeSet<>();
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeLimitOrderProcessor.processDirectedLimitOrder(limitOrder, tickerData, marketOrders, sameTypeLimitOrders, oppositeTypeLimitOrders);

    //Then: queueIfTimeInForce is called with the right parameters
    verify(marketUtils).queueIfTimeInForce(eq(limitOrder), eq(sameTypeLimitOrders), any());
  }

  @Test
  public void whenMarketQueueEmptyNonMatchingLimitOrderInLimitQueue() {
    //Given: empty market order and a non matching limit order in limit order queue

    //When: process is called

    //Then: queueIfTimeInForce is called with the right parameters

  }
}
