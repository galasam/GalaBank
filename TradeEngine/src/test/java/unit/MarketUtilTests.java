package unit;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.LimitOrder;
import com.gala.sam.tradeEngine.domain.OrderReq.Order;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.dataStructures.LimitOrderQueue;
import com.gala.sam.tradeEngine.domain.dataStructures.LimitOrderQueue.SORTING_METHOD;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import org.junit.Assert;
import org.junit.Test;

import java.util.SortedSet;
import java.util.TreeSet;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;
import static org.mockito.Mockito.*;

public class MarketUtilTests {

    @Test
    public void testQueueIfTimeInForce() {
        SortedSet<LimitOrder> orders = new LimitOrderQueue(SORTING_METHOD.PRICE_ASC);
        LimitOrder order = LimitOrder.builder().timeInForce(Order.TIME_IN_FORCE.GTC).orderId(1).build();
        queueIfTimeInForce(order, orders);
        Assert.assertEquals("", 1, orders.size());
    }

    @Test
    public void testQueueIfTimeNotInForce() {
        SortedSet<LimitOrder> orders = new TreeSet<>();
        LimitOrder order = LimitOrder.builder().timeInForce(Order.TIME_IN_FORCE.FOK).build();
        queueIfTimeInForce(order, orders);
        Assert.assertEquals("", 0, orders.size());
    }

    @Test
    public void testMakeTradeCreatesTradeCorrectly() {
        TickerData tickerData = mock(TickerData.class);
        MarketState marketState = mock(MarketState.class);

        LimitOrder limitOrderA = LimitOrder.builder()
                .orderId(1)
                .direction(Order.DIRECTION.BUY)
                .ticker("XXX")
                .timeInForce(Order.TIME_IN_FORCE.GTC)
                .quantity(10)
                .clientId(100)
                .build();

        LimitOrder limitOrderB = LimitOrder.builder()
                .orderId(2)
                .direction(Order.DIRECTION.SELL)
                .ticker("XXX")
                .timeInForce(Order.TIME_IN_FORCE.GTC)
                .quantity(100)
                .clientId(101)
                .build();

        Trade trade = Trade.builder()
                .buyOrder(limitOrderA.getOrderId())
                .sellOrder(limitOrderB.getOrderId())
                .ticker(limitOrderA.getTicker())
                .matchPrice(limitOrderA.getLimit())
                .matchQuantity(Math.min(limitOrderA.getQuantityRemaining(), limitOrderB.getQuantityRemaining()))
                .build();

        makeTrade(marketState, limitOrderA, limitOrderB, limitOrderA.getLimit(), tickerData);

        verify(tickerData).setLastExecutedTradePrice(limitOrderA.getLimit());
        verify(marketState).addTrade(trade);
        Assert.assertTrue("LimitOrderA should be fully fulfilled.", limitOrderA.isFullyFulfilled());
        Assert.assertTrue("LimitOrderB should not be fully fulfilled.", !limitOrderB.isFullyFulfilled());
        Assert.assertEquals("LimitOrderB should have shares remaining.", 90, limitOrderB.getQuantityRemaining());
    }

}
