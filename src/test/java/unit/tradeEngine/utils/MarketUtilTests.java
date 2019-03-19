package unit.tradeEngine.utils;

import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;

import com.gala.sam.tradeEngine.domain.LimitOrder;
import com.gala.sam.tradeEngine.domain.ReadyOrder.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.domain.dataStructures.LimitOrderQueue;
import com.gala.sam.tradeEngine.domain.dataStructures.LimitOrderQueue.SORTING_METHOD;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;

public class MarketUtilTests {

    @Test
    public void testQueueIfTimeInForce() {
        SortedSet<LimitOrder> orders = new LimitOrderQueue(SORTING_METHOD.PRICE_ASC);
        LimitOrder order = LimitOrder.builder().timeInForce(TIME_IN_FORCE.GTC).orderId(1).build();
        queueIfTimeInForce(order, orders);
        Assert.assertEquals("", 1, orders.size());
    }

    @Test
    public void testQueueIfTimeNotInForce() {
        SortedSet<LimitOrder> orders = new TreeSet<>();
        LimitOrder order = LimitOrder.builder().timeInForce(TIME_IN_FORCE.FOK).build();
        queueIfTimeInForce(order, orders);
        Assert.assertEquals("", 0, orders.size());
    }

}
