package com.gala.sam.tradeengine.utils.orderprocessors;

import static com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction.BUY;
import static org.mockito.BDDMockito.given;

import com.gala.sam.tradeengine.domain.PublicMarketStatus;
import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.domain.datastructures.MarketState;
import com.gala.sam.tradeengine.domain.datastructures.TickerData;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.service.MarketService;
import com.gala.sam.tradeengine.utils.MarketUtils;
import com.gala.sam.tradeengine.utils.enteredordergenerators.EnteredOrderGeneratorFactory;
import com.gala.sam.tradeengine.utils.orderprocessors.OrderProcessorFactory;
import com.gala.sam.tradeengine.utils.ordervalidators.OrderValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MarketStatusReturnTests {

  @TestConfiguration
  static class Config {
    @Autowired
    ITradeRepository tradeRepository;
    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    EnteredOrderGeneratorFactory enteredOrderGeneratorFactory;
    @Autowired
    OrderProcessorFactory orderProcessorFactory;
    @Autowired
    OrderValidatorFactory orderValidatorFactory;
    @Autowired
    MarketUtils marketUtils;
    @Autowired
    MarketState marketState;

    @Bean
    public MarketService marketService() {
      return new MarketService(tradeRepository, orderRepository, enteredOrderGeneratorFactory,
          orderProcessorFactory, orderValidatorFactory, marketUtils, marketState);
    }
  }

  @MockBean
  ITradeRepository tradeRepository;
  @MockBean
  IOrderRepository orderRepository;
  @MockBean
  EnteredOrderGeneratorFactory enteredOrderGeneratorFactory;
  @MockBean
  OrderProcessorFactory orderProcessorFactory;
  @MockBean
  OrderValidatorFactory orderValidatorFactory;
  @MockBean
  MarketUtils marketUtils;
  @MockBean
  MarketState marketState;

  @Autowired
  MarketService marketService;

  @Test
  public void orderEnteredIsShownInStatus() {
    //Given: an order in the ticker queue
    LimitOrder limitOrder = getLimitOrder(BUY);
    TickerData tickerData = new TickerData(limitOrder.getTicker());
    tickerData.getBuyLimitOrders().add(limitOrder);
    Map<String, TickerData> tickerQueues = new TreeMap<>();
    tickerQueues.put(limitOrder.getTicker(), tickerData);
    given(marketState.getTickerQueues()).willReturn(tickerQueues);
    given(marketState.getTrades()).willReturn(new ArrayList<>());

    //When: get market status
    PublicMarketStatus publicMarketStatus = marketService.getStatus();

    //Then: it is visible in the correct place in the market status
    Assert.assertTrue("A ticker is returned", publicMarketStatus.getOrders().size() > 0);
    Assert.assertTrue("A buy order is returned",
        publicMarketStatus.getOrders().get(0).getBuy().size() > 0);
    Assert.assertEquals("It is the correct order", limitOrder,
        publicMarketStatus.getOrders().get(0).getBuy().get(0));
  }

  private LimitOrder getLimitOrder(Direction direction) {
    return LimitOrder.builder()
        .orderId(1)
        .clientId(1)
        .direction(direction)
        .quantity(1)
        .ticker("Fred")
        .timeInForce(TimeInForce.GTC)
        .limit(1f)
        .build();
  }

  @Test
  public void orderTradeIsShownInStatus() {
    //Given: a trade in the trades list
    Trade trade = getTrade();
    List<Trade> trades = new ArrayList<>();
    trades.add(trade);
    given(marketState.getTrades()).willReturn(trades);
    given(marketState.getTickerQueues()).willReturn(new TreeMap<>());

    //When: they are entered on the market
    PublicMarketStatus publicMarketStatus = marketService.getStatus();

    //Then: the resulting trade is visible in the correct place in the
    //market status and the orders are removed
    Assert.assertTrue("A trade is shown", publicMarketStatus.getTrades().size() > 0);
    Assert.assertEquals("It is the correct trade", trade, publicMarketStatus.getTrades().get(0));
    Assert.assertEquals("No orders are shown", 0, publicMarketStatus.getOrders().size());
  }

  private Trade getTrade() {
    return Trade.builder()
        .buyOrder(1)
        .sellOrder(2)
        .ticker("Greggs")
        .matchQuantity(10)
        .matchPrice(1.2f)
        .build();
  }
}
