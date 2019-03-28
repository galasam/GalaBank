package unit;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.EnteredOrder.LimitOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.dataStructures.LimitOrderQueue;
import com.gala.sam.tradeEngine.domain.dataStructures.LimitOrderQueue.SORTING_METHOD;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;

public class MarketUtilTests {

  @Test
  public void testQueueIfTimeInForce() {
    SortedSet<LimitOrder> orders = new LimitOrderQueue(SORTING_METHOD.PRICE_ASC);
    LimitOrder order = LimitOrder.builder().timeInForce(TIME_IN_FORCE.GTC).orderId(1).build();
    Consumer<Order> save = mock(Consumer.class);

    queueIfTimeInForce(order, orders, save);

    verify(save).accept(order);
    Assert.assertEquals("", 1, orders.size());
  }

  @Test
  public void testQueueIfTimeNotInForce() {
    SortedSet<LimitOrder> orders = new TreeSet<>();
    LimitOrder order = LimitOrder.builder().timeInForce(TIME_IN_FORCE.FOK).build();
    Consumer<Order> save = mock(Consumer.class);

    queueIfTimeInForce(order, orders, save);

    verify(save).accept(order);
    Assert.assertEquals("", 0, orders.size());
  }

  @Test
  public void testMakeTradeCreatesTradeCorrectly() {
    TickerData tickerData = mock(TickerData.class);
    MarketState marketState = mock(MarketState.class);

    LimitOrder limitOrderA = LimitOrder.builder()
        .orderId(1)
        .direction(DIRECTION.BUY)
        .ticker("XXX")
        .timeInForce(TIME_IN_FORCE.GTC)
        .quantity(10)
        .clientId(100)
        .build();

    LimitOrder limitOrderB = LimitOrder.builder()
        .orderId(2)
        .direction(DIRECTION.SELL)
        .ticker("XXX")
        .timeInForce(TIME_IN_FORCE.GTC)
        .quantity(100)
        .clientId(101)
        .build();

    Trade trade = Trade.builder()
        .buyOrder(limitOrderA.getOrderId())
        .sellOrder(limitOrderB.getOrderId())
        .ticker(limitOrderA.getTicker())
        .matchPrice(limitOrderA.getLimit())
        .matchQuantity(
            Math.min(limitOrderA.getQuantityRemaining(), limitOrderB.getQuantityRemaining()))
        .build();

    Consumer<Trade> save = mock(Consumer.class);

    makeTrade(marketState, limitOrderA, limitOrderB, limitOrderA.getLimit(), tickerData, save);

    verify(tickerData).setLastExecutedTradePrice(limitOrderA.getLimit());
    verify(marketState).getTrades().add(trade);
    Assert.assertTrue("LimitOrderA should be fully fulfilled.", limitOrderA.isFullyFulfilled());
    Assert
        .assertTrue("LimitOrderB should not be fully fulfilled.", !limitOrderB.isFullyFulfilled());
    Assert.assertEquals("LimitOrderB should have shares remaining.", 90,
        limitOrderB.getQuantityRemaining());
  }

}
