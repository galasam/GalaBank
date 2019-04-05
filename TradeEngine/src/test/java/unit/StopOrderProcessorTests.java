package unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.EnteredOrder.StopLimitOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.StopOrder;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import com.gala.sam.tradeEngine.utils.OrderProcessor.StopOrderProcessor;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org.junit.Test;

public class StopOrderProcessorTests {

  @Test
  public void stopOrderIsAddedToStopOrderList() {
    //Given a stop order
    StopOrder stopOrder = StopLimitOrder.builder().build();
    List<StopOrder> stopOrders = mock(List.class);
    MarketState marketState = new MarketState(new LinkedList<>(), new TreeMap<>(), stopOrders);
    OrderRepository orderRepository = mock(OrderRepository.class);
    StopOrderProcessor stopOrderProcessor = new StopOrderProcessor(orderRepository, RepositoryMockHelper.getEmptyRepository(TradeRepository.class), marketState);
    //When: it is processed
    stopOrderProcessor.process(stopOrder);
    //Then: the order is added to the market state stop orders and saved
    verify(stopOrders).add(stopOrder);
    verify(orderRepository).save(stopOrder);
  }
}
