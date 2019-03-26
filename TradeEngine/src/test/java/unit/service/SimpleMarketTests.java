package unit.service;

import com.gala.sam.tradeEngine.domain.LimitOrder;
import com.gala.sam.tradeEngine.domain.MarketOrder;
import com.gala.sam.tradeEngine.domain.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.Order.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.service.MarketService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SimpleMarketTests {

    @Test
    public void testSimpleTimeStep() {

        MarketService marketService = new MarketService();

        LimitOrder limitOrder = LimitOrder.builder()
            .orderId(1)
            .direction(DIRECTION.BUY)
            .quantity(999)
            .ticker("Fred")
            .limit(3.14f)
            .timeInForce(TIME_IN_FORCE.GTC)
            .build();

        MarketOrder marketOrder = MarketOrder.builder()
            .orderId(2)
            .direction(DIRECTION.SELL)
            .quantity(999)
            .ticker("Fred")
            .timeInForce(TIME_IN_FORCE.GTC)
            .build();

        Trade tradeOutputTest = Trade.builder()
            .buyOrder(limitOrder.getOrderId())
            .sellOrder(marketOrder.getOrderId())
            .matchQuantity(marketOrder.getQuantity())
            .matchPrice(limitOrder.getLimit())
            .build();

        marketService.enterOrder(limitOrder);
        marketService.enterOrder(marketOrder);

        List<Trade> trades = marketService.getAllMatchedTrades();

        Assert.assertEquals("Should be able to match a buy limit and sell market order", 1,
            trades.size());
        Assert.assertEquals("Should match should be correct", trades.get(0), tradeOutputTest);
    }

    @Test
    public void testTimeStepWithMatchingLimits() {

        MarketService marketService = new MarketService();

        LimitOrder limitOrderA = LimitOrder.builder()
            .orderId(1)
            .direction(DIRECTION.BUY)
            .quantity(999)
            .ticker("Fred")
            .limit(3.14f)
            .timeInForce(TIME_IN_FORCE.GTC)
            .build();

        LimitOrder limitOrderBMatchingA = LimitOrder.builder()
            .orderId(2)
            .direction(DIRECTION.SELL)
            .quantity(999)
            .ticker("Fred")
            .limit(2f)
            .timeInForce(TIME_IN_FORCE.GTC)
            .build();

        Trade tradeOutputTest = Trade.builder()
            .buyOrder(limitOrderA.getOrderId())
            .sellOrder(limitOrderBMatchingA.getOrderId())
            .matchQuantity(limitOrderA.getQuantity())
            .matchPrice(limitOrderA.getLimit())
            .build();


        marketService.enterOrder(limitOrderA);
        marketService.enterOrder(limitOrderBMatchingA);
        List<Trade> trades = marketService.getAllMatchedTrades();

        Assert.assertEquals("Should be able to match a buy and sell matching limit orders", 1,
            trades.size());
        Assert.assertEquals("Should match should be correct", trades.get(0), tradeOutputTest);
    }


    @Test
    public void testTimeStepWithNonMatchingLimits() {

        MarketService marketService = new MarketService();

        LimitOrder limitOrderA = LimitOrder.builder()
            .orderId(1)
            .direction(DIRECTION.BUY)
            .quantity(999)
            .ticker("Fred")
            .limit(3.14f)
            .timeInForce(TIME_IN_FORCE.GTC)
            .build();

        LimitOrder limitOrderBNotMatchingA = LimitOrder.builder()
            .orderId(2)
            .direction(DIRECTION.SELL)
            .quantity(999)
            .ticker("Fred")
            .limit(10f)
            .timeInForce(TIME_IN_FORCE.GTC)
            .build();

        marketService.enterOrder(limitOrderA);
        marketService.enterOrder(limitOrderBNotMatchingA);
        List<Trade> trades = marketService.getAllMatchedTrades();

        Assert.assertEquals("Should not match a buy and sell non-matching limit orders", 0,
            trades.size());
    }


}
