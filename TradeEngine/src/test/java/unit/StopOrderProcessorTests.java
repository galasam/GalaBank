package unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractStopOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.StopLimitOrder;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.orderProcessors.StopOrderProcessor;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org.junit.Test;

public class StopOrderProcessorTests {

  @Test
  public void stopOrderIsAddedToStopOrderList() {
    //Given a stop order
    AbstractStopOrder stopOrder = StopLimitOrder.builder().build();
    List<AbstractStopOrder> stopOrders = mock(List.class);
    MarketState marketState = new MarketState(new LinkedList<>(), new TreeMap<>(), stopOrders);
    IOrderRepository orderRepository = mock(IOrderRepository.class);
    MarketUtils marketUtils = mock(MarketUtils.class);
    StopOrderProcessor stopOrderProcessor = new StopOrderProcessor(orderRepository,
        RepositoryMockHelper.getEmptyRepository(ITradeRepository.class), marketState, marketUtils);
    //When: it is processed
    stopOrderProcessor.process(stopOrder);
    //Then: the order is added to the market state stop orders and saved
    verify(stopOrders).add(stopOrder);
    verify(orderRepository).save(stopOrder);
  }
}
