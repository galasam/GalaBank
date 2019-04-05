package unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.EnteredOrder.LimitOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.MarketOrder;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.OrderProcessor.ActiveLimitOrderProcessor;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;

public class LimitOrderProcessorTests {

  @Test
  public void whenAllQueuesAreEmptyTest() {
    //Given a limit order and empty queues
    LimitOrder limitOrder = LimitOrder.builder().build();

    OrderRepository orderRepository = RepositoryMockHelper.getEmptyRepository(OrderRepository.class);
    TradeRepository tradeRepository = RepositoryMockHelper.getEmptyRepository(TradeRepository.class);
    MarketState marketState = mock(MarketState.class);
    MarketUtils marketUtils = mock(MarketUtils.class);

    ActiveLimitOrderProcessor activeLimitOrderProcessor = new ActiveLimitOrderProcessor(
        orderRepository, tradeRepository, marketState, marketUtils);

    SortedSet<MarketOrder> marketOrders = new TreeSet<>();
    SortedSet<LimitOrder> sameTypeLimitOrders = new TreeSet<>();
    SortedSet<LimitOrder> oppositeTypeLimitOrders = new TreeSet<>();
    TickerData tickerData = mock(TickerData.class);

    //When: process is called
    activeLimitOrderProcessor.processDirectedLimitOrder(limitOrder, tickerData, marketOrders, sameTypeLimitOrders, oppositeTypeLimitOrders);

    //Then: queueIfTimeInForce is called with the right parameters
    verify(marketUtils).queueIfTimeInForce(limitOrder, sameTypeLimitOrders, any());
  }

  @Test
  public void whenMarketQueueEmptyNonMatchingLimitOrderInLimitQueue() {
    //Given: empty market order and a non matching limit order in limit order queue

    //When: process is called

    //Then: queueIfTimeInForce is called with the right parameters

  }
}
