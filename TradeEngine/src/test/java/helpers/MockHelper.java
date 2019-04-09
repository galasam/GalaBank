package helpers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorFactory;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorState;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorFactory;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorUtils;
import com.gala.sam.tradeEngine.utils.orderValidators.OrderValidatorFactory;
import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;

public class MockHelper {

  public static <T extends CrudRepository> T getEmptyRepository(Class<T> type) {
    T repository = mock(type);
    when(repository.findAll()).thenReturn(new ArrayList<>());
    return repository;
  }

  public static OrderProcessorFactory getOrderProcessorFactor() {
    return new OrderProcessorFactory(
        getEmptyRepository(ITradeRepository.class),
        getEmptyRepository(IOrderRepository.class),
        new MarketUtils(), new OrderProcessorUtils());
  }

  public static MarketService getMarketService() {
    return new MarketService(
        MockHelper.getEmptyRepository(ITradeRepository.class),
        MockHelper.getEmptyRepository(IOrderRepository.class),
        new EnteredOrderGeneratorFactory(new EnteredOrderGeneratorState()),
        getOrderProcessorFactor(),
        new OrderValidatorFactory(),
        new MarketUtils());
  }

}
