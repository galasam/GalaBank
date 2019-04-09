package unit;

import static com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction.BUY;
import static com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction.SELL;

import com.gala.sam.tradeEngine.domain.PublicMarketStatus;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.domain.orderrequest.LimitOrderRequest;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorFactory;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorState;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorFactory;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorUtils;
import com.gala.sam.tradeEngine.utils.orderValidators.OrderValidatorFactory;
import helpers.MockHelper;
import org.junit.Assert;
import org.junit.Test;

public class MarketStatusReturnTests {

  @Test
  public void orderEnteredIsShownInStatus() {
    //Given: an order
    OrderProcessorFactory orderProcessorFactory = new OrderProcessorFactory(
        MockHelper.getEmptyRepository(ITradeRepository.class),
        MockHelper.getEmptyRepository(IOrderRepository.class),
        new MarketUtils(), new OrderProcessorUtils());
    MarketService marketService = new MarketService(
        MockHelper.getEmptyRepository(ITradeRepository.class),
        MockHelper.getEmptyRepository(IOrderRepository.class),
        new EnteredOrderGeneratorFactory(new EnteredOrderGeneratorState()),
        orderProcessorFactory,
        new OrderValidatorFactory(),
        new MarketUtils());

    LimitOrderRequest limitOrderReq = getLimitOrderReq(BUY);

    //When: order is entered to market
    AbstractOrder limitOrder = marketService.enterOrder(limitOrderReq).get();

    //Then: it is visible in the correct place in the market status
    PublicMarketStatus publicMarketStatus = marketService.getStatus();

    Assert.assertTrue("A ticker is returned", publicMarketStatus.getOrders().size() > 0);
    Assert.assertTrue("A buy order is returned",
        publicMarketStatus.getOrders().get(0).getBuy().size() > 0);
    Assert.assertEquals("It is the correct order", limitOrder,
        publicMarketStatus.getOrders().get(0).getBuy().get(0));
  }

  private LimitOrderRequest getLimitOrderReq(Direction direction) {
    return LimitOrderRequest.builder()
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
    //Given: two matching trades
    OrderProcessorFactory orderProcessorFactory = new OrderProcessorFactory(
        MockHelper.getEmptyRepository(ITradeRepository.class),
        MockHelper.getEmptyRepository(IOrderRepository.class),
        new MarketUtils(), new OrderProcessorUtils());
    MarketService marketService = new MarketService(
        MockHelper.getEmptyRepository(ITradeRepository.class),
        MockHelper.getEmptyRepository(IOrderRepository.class),
        new EnteredOrderGeneratorFactory(new EnteredOrderGeneratorState()),
        orderProcessorFactory,
        new OrderValidatorFactory(),
        new MarketUtils());

    LimitOrderRequest buyLimitOrderReq = getLimitOrderReq(BUY);
    LimitOrderRequest sellLimitOrderReq = getLimitOrderReq(SELL);

    //When: they are entered on the market
    AbstractOrder buyLimitOrder = marketService.enterOrder(buyLimitOrderReq).get();
    AbstractOrder sellLimitOrder = marketService.enterOrder(sellLimitOrderReq).get();

    //Then: the resulting trade is visible in the correct place in the
    //market status and the orders are removed
    PublicMarketStatus publicMarketStatus = marketService.getStatus();

    Trade trade = Trade.builder()
        .matchQuantity(buyLimitOrderReq.getQuantity())
        .matchPrice(buyLimitOrderReq.getLimit())
        .buyOrder(buyLimitOrder.getOrderId())
        .sellOrder(sellLimitOrder.getOrderId())
        .ticker(buyLimitOrder.getTicker())
        .build();

    Assert.assertTrue("A trade is shown", publicMarketStatus.getTrades().size() > 0);
    Assert.assertEquals("It is the correct trade", trade, publicMarketStatus.getTrades().get(0));
    Assert.assertEquals("No orders are shown", 0, publicMarketStatus.getOrders().size());
  }
}
