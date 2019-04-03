package unit;

import static com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction.BUY;
import static com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction.SELL;

import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.domain.orderrequest.LimitOrderRequest;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.OrderProcessor.OrderProcessorFactory;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorFactory;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorState;
import com.gala.sam.tradeEngine.utils.orderValidators.OrderValidatorFactory;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class MarketStatusReturnTests {

  @Test
  public void orderEnteredIsShownInStatus() {

    val orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(ITradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(IOrderRepository.class));
    MarketService marketService = new MarketService(
        RepositoryMockHelper.getEmptyRepository(ITradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(IOrderRepository.class),
        new EnteredOrderGeneratorFactory(new EnteredOrderGeneratorState()),
        orderProcessorFactory,
        new OrderValidatorFactory());

    LimitOrderRequest limitOrderReq = getLimitOrderReq(BUY);

    val limitOrder = marketService.enterOrder(limitOrderReq).get();
    val publicMarketStatus = marketService.getStatus();

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

    val orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(ITradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(IOrderRepository.class));
    MarketService marketService = new MarketService(
        RepositoryMockHelper.getEmptyRepository(ITradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(IOrderRepository.class),
        new EnteredOrderGeneratorFactory(new EnteredOrderGeneratorState()),
        orderProcessorFactory,
        new OrderValidatorFactory());

    val buyLimitOrderReq = getLimitOrderReq(BUY);
    val sellLimitOrderReq = getLimitOrderReq(SELL);

    val buyLimitOrder = marketService.enterOrder(buyLimitOrderReq).get();
    val sellLimitOrder = marketService.enterOrder(sellLimitOrderReq).get();
    val publicMarketStatus = marketService.getStatus();

    val trade = Trade.builder()
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
