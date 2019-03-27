package unit.service;

import com.gala.sam.tradeEngine.domain.OrderReq.LimitOrder;
import com.gala.sam.tradeEngine.domain.OrderReq.MarketOrder;
import com.gala.sam.tradeEngine.domain.OrderReq.Order;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import com.gala.sam.tradeEngine.service.MarketService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleMarketTests {

    @Test
    public void testSimpleTimeStep() {

        TradeRepository tradeRepository = mock(TradeRepository.class);
        when(tradeRepository.findAll()).thenReturn(new ArrayList<>());
        MarketService marketService = new MarketService(tradeRepository);

        LimitOrder limitOrder = LimitOrder.builder()
            .direction(Order.DIRECTION.BUY)
            .quantity(999)
            .ticker("Fred")
            .limit(3.14f)
            .timeInForce(Order.TIME_IN_FORCE.GTC)
            .build();

        MarketOrder marketOrder = MarketOrder.builder()
            .direction(Order.DIRECTION.SELL)
            .quantity(999)
            .ticker("Fred")
            .timeInForce(Order.TIME_IN_FORCE.GTC)
            .build();

        int limitOrderId = marketService.enterOrder(limitOrder).getOrderId();
        int marketOrderId = marketService.enterOrder(marketOrder).getOrderId();

        List<Trade> trades = marketService.getAllMatchedTrades();

        Trade tradeOutputTest = Trade.builder()
                .buyOrder(limitOrderId)
                .sellOrder(marketOrderId)
                .matchQuantity(marketOrder.getQuantity())
                .matchPrice(limitOrder.getLimit())
                .ticker(limitOrder.getTicker())
                .build();

        Assert.assertEquals("Should be able to match a buy limit and sell market order", 1,
            trades.size());
        Assert.assertEquals("Should match should be correct", trades.get(0), tradeOutputTest);
    }

    @Test
    public void testTimeStepWithMatchingLimits() {

        TradeRepository tradeRepository = mock(TradeRepository.class);
        when(tradeRepository.findAll()).thenReturn(new ArrayList<>());
        MarketService marketService = new MarketService(tradeRepository);

        LimitOrder limitOrderA = LimitOrder.builder()
            .direction(Order.DIRECTION.BUY)
            .quantity(999)
            .ticker("Fred")
            .limit(3.14f)
            .timeInForce(Order.TIME_IN_FORCE.GTC)
            .build();

        LimitOrder limitOrderBMatchingA = LimitOrder.builder()
            .direction(Order.DIRECTION.SELL)
            .quantity(999)
            .ticker("Fred")
            .limit(2f)
            .timeInForce(Order.TIME_IN_FORCE.GTC)
            .build();

        int orderIdA = marketService.enterOrder(limitOrderA).getOrderId();
        int orderIdB = marketService.enterOrder(limitOrderBMatchingA).getOrderId();

        List<Trade> trades = marketService.getAllMatchedTrades();

        Trade tradeOutputTest = Trade.builder()
                .buyOrder(orderIdA)
                .sellOrder(orderIdB)
                .matchQuantity(limitOrderA.getQuantity())
                .matchPrice(limitOrderA.getLimit())
                .ticker(limitOrderA.getTicker())
                .build();

        Assert.assertEquals("Should be able to match a buy and sell matching limit orders", 1,
            trades.size());
        Assert.assertEquals("Should match should be correct", trades.get(0), tradeOutputTest);
    }

    @Test
    public void testTimeStepWithNonMatchingLimits() {

        TradeRepository tradeRepository = mock(TradeRepository.class);
        when(tradeRepository.findAll()).thenReturn(new ArrayList<>());
        MarketService marketService = new MarketService(tradeRepository);

        LimitOrder limitOrderA = LimitOrder.builder()
            .direction(Order.DIRECTION.BUY)
            .quantity(999)
            .ticker("Fred")
            .limit(3.14f)
            .timeInForce(Order.TIME_IN_FORCE.GTC)
            .build();

        LimitOrder limitOrderBNotMatchingA = LimitOrder.builder()
            .direction(Order.DIRECTION.SELL)
            .quantity(999)
            .ticker("Fred")
            .limit(10f)
            .timeInForce(Order.TIME_IN_FORCE.GTC)
            .build();

        marketService.enterOrder(limitOrderA);
        marketService.enterOrder(limitOrderBNotMatchingA);
        List<Trade> trades = marketService.getAllMatchedTrades();

        Assert.assertEquals("Should not match a buy and sell non-matching limit orders", 0,
            trades.size());
    }

}
