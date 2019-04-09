package unit;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfGTC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue;
import com.gala.sam.tradeEngine.domain.datastructures.LimitOrderQueue.SortingMethod;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderTimeInForceNotSupportedException;
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
  public void testQueueIfTimeInForce() throws OrderTimeInForceNotSupportedException {
    //Given: GTC order
    SortedSet<LimitOrder> orders = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    LimitOrder order = LimitOrder.builder().timeInForce(TimeInForce.GTC).orderId(1).build();
    Consumer<AbstractOrder> save = mock(Consumer.class);

    //When queueIfGTC is called
    queueIfGTC(order, orders, save);

    /*Then:
      - Order should be saved
      - order should be added to queue
     */
    verify(save).accept(order);
    Assert.assertEquals("", 1, orders.size());
  }

  @Test
  public void testQueueIfTimeNotInForce() throws OrderTimeInForceNotSupportedException {
    //Given: FOK order
    SortedSet<LimitOrder> orders = new TreeSet<>();
    LimitOrder order = LimitOrder.builder().timeInForce(TimeInForce.FOK).build();
    Consumer<AbstractOrder> save = mock(Consumer.class);

    //When queueIfGTC is called
    queueIfGTC(order, orders, save);

    /*Then:
     - order should not be saved
     - order should not be added to queue
     */
    verify(save, never()).accept(any());
    Assert.assertEquals("", 0, orders.size());
  }

  @Test
  public void testMakeTradeCreatesTradeCorrectly() throws OrderDirectionNotSupportedException {
    //Given: Two limit orders that should match
    TickerData tickerData = mock(TickerData.class);
    List<Trade> trades = mock(List.class);
    MarketState marketState = new MarketState(trades, new TreeMap<>(), new LinkedList<>());

    LimitOrder limitOrderA = LimitOrder.builder()
        .orderId(1)
        .direction(Direction.BUY)
        .ticker("XXX")
        .timeInForce(TimeInForce.GTC)
        .quantity(10)
        .clientId(100)
        .build();

    LimitOrder limitOrderB = LimitOrder.builder()
        .orderId(2)
        .direction(Direction.SELL)
        .ticker("XXX")
        .timeInForce(TimeInForce.GTC)
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

    //When makeTrade is called
    makeTrade(marketState, limitOrderA, limitOrderB, limitOrderA.getLimit(), tickerData, save);

    /*Then:
     - LastExecutedTradePrice is updated
     - the trade is added to the queue
     - the orders are set to be fully satisfied
     */
    verify(tickerData, times(1)).setLastExecutedTradePrice(limitOrderA.getLimit());
    verify(marketState.getTrades(), times(1)).add(trade);
    Assert.assertTrue("LimitOrderA should be fully fulfilled.", limitOrderA.isFullyFulfilled());
    Assert
        .assertTrue("LimitOrderB should not be fully fulfilled.", !limitOrderB.isFullyFulfilled());
    Assert.assertEquals("LimitOrderB should have shares remaining.", 90,
        limitOrderB.getQuantityRemaining());
  }

}
