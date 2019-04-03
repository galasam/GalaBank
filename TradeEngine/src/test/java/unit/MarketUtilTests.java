package unit;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue.SORTING_METHOD;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;

public class MarketUtilTests {

  @Test
  public void testQueueIfTimeInForce() {
    SortedSet<LimitOrder> orders = new LimitOrderQueue(SORTING_METHOD.PRICE_ASC);
    LimitOrder order = LimitOrder.builder().timeInForce(TIME_IN_FORCE.GTC).orderId(1).build();
    Consumer<AbstractOrder> save = mock(Consumer.class);

    queueIfTimeInForce(order, orders, save);

    verify(save).accept(order);
    Assert.assertEquals("", 1, orders.size());
  }

  @Test
  public void testQueueIfTimeNotInForce() {
    SortedSet<LimitOrder> orders = new TreeSet<>();
    LimitOrder order = LimitOrder.builder().timeInForce(TIME_IN_FORCE.FOK).build();
    Consumer<AbstractOrder> save = mock(Consumer.class);

    queueIfTimeInForce(order, orders, save);

    verify(save, never()).accept(any());
    Assert.assertEquals("", 0, orders.size());
  }

  @Test
  public void testMakeTradeCreatesTradeCorrectly() {
    TickerData tickerData = mock(TickerData.class);
    List<Trade> trades = mock(List.class);
    MarketState marketState = new MarketState(trades, new TreeMap<>(), new LinkedList<>());

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

    verify(tickerData, times(1)).setLastExecutedTradePrice(limitOrderA.getLimit());
    verify(marketState.getTrades(), times(1)).add(trade);
    Assert.assertTrue("LimitOrderA should be fully fulfilled.", limitOrderA.isFullyFulfilled());
    Assert
        .assertTrue("LimitOrderB should not be fully fulfilled.", !limitOrderB.isFullyFulfilled());
    Assert.assertEquals("LimitOrderB should have shares remaining.", 90,
        limitOrderB.getQuantityRemaining());
  }

}
