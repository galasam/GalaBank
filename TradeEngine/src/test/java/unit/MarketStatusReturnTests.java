package unit;

import static com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.DIRECTION.BUY;
import static com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.DIRECTION.SELL;
import com.gala.sam.tradeEngine.domain.OrderReq.LimitOrderReq;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.ConcreteOrderGenerator;
import com.gala.sam.tradeEngine.utils.OrderProcessor.OrderProcessorFactory;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class MarketStatusReturnTests {

  @Test
  public void orderEnteredIsShownInStatus() {

    val orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class));
    val marketService = new MarketService(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class),
        new ConcreteOrderGenerator(),
        orderProcessorFactory);

    LimitOrderReq limitOrderReq = getLimitOrderReq(BUY);

    val limitOrder = marketService.enterOrder(limitOrderReq);
    val publicMarketStatus = marketService.getStatus();

    Assert.assertTrue("A ticker is returned", publicMarketStatus.getOrders().size() > 0);
    Assert.assertTrue("A buy order is returned", publicMarketStatus.getOrders().get(0).getBuy().size() > 0);
    Assert.assertEquals("It is the correct order", limitOrder, publicMarketStatus.getOrders().get(0).getBuy().get(0));
  }

  private LimitOrderReq getLimitOrderReq(DIRECTION direction) {
    return LimitOrderReq.builder()
        .clientId(1)
        .direction(direction)
        .quantity(1)
        .ticker("Fred")
        .timeInForce(TIME_IN_FORCE.GTC)
        .limit(1f)
        .build();
  }

  @Test
  public void orderTradeIsShownInStatus() {

    val orderProcessorFactory = new OrderProcessorFactory(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class));
    val marketService = new MarketService(
        RepositoryMockHelper.getEmptyRepository(TradeRepository.class),
        RepositoryMockHelper.getEmptyRepository(OrderRepository.class),
        new ConcreteOrderGenerator(),
        orderProcessorFactory);

    val buyLimitOrderReq = getLimitOrderReq(BUY);
    val sellLimitOrderReq = getLimitOrderReq(SELL);

    val buyLimitOrder = marketService.enterOrder(buyLimitOrderReq);
    val sellLimitOrder = marketService.enterOrder(sellLimitOrderReq);
    val publicMarketStatus = marketService.getStatus();

    val trade = Trade.builder()
        .matchQuantity(buyLimitOrderReq.getQuantity())
        .matchPrice(buyLimitOrderReq.getLimit())
        .buyOrder(buyLimitOrder.getOrderId())
        .sellOrder(sellLimitOrder.getOrderId())
        .ticker(buyLimitOrder.getTicker())
        .build();
    
    Assert.assertTrue("A trade is shown", publicMarketStatus.getTrades().size() > 0);
    Assert.assertEquals("It is the correct trade", trade, publicMarketStatus.getOrders().get(0).getBuy().get(0));
    Assert.assertEquals("No orders are shown", 0, publicMarketStatus.getOrders().size());
  }
}
