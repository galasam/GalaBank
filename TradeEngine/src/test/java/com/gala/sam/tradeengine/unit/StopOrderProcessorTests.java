package com.gala.sam.tradeengine.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeengine.domain.datastructures.MarketState;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractStopOrder;
import com.gala.sam.tradeengine.domain.enteredorder.StopLimitOrder;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.utils.MarketUtils;
import com.gala.sam.tradeengine.utils.orderprocessors.StopOrderProcessor;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class StopOrderProcessorTests {

  @TestConfiguration
  static class Config {

    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    ITradeRepository tradeRepository;
    @Autowired
    MarketUtils marketUtils;

    @Bean
    public StopOrderProcessor stopOrderProcessor() {
      return new StopOrderProcessor(marketUtils, orderRepository, tradeRepository);
    }
  }

  @MockBean
  IOrderRepository orderRepository;
  @MockBean
  ITradeRepository tradeRepository;
  @MockBean
  MarketUtils marketUtils;

  @Autowired
  StopOrderProcessor stopOrderProcessor;

  @Test
  public void stopOrderIsAddedToStopOrderList() {
    //Given a stop order
    AbstractStopOrder stopOrder = StopLimitOrder.builder().build();
    List<AbstractStopOrder> stopOrders = mock(List.class);
    MarketState marketState = MarketState.injectStopOrders(stopOrders);
    //When: it is processed
    stopOrderProcessor.process(marketState, stopOrder);
    //Then: the order is added to the market state stop orders and saved
    verify(stopOrders).add(stopOrder);
    verify(orderRepository).save(stopOrder);
  }
}
