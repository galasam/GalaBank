package com.gala.sam.tradeEngine.domain.dataStructures;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.LimitOrder;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.MarketOrder;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.ActiveOrder;
import com.gala.sam.tradeEngine.domain.dataStructures.LimitOrderQueue.SORTING_METHOD;

import java.util.Comparator;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

public class TickerData {
    private final SortedSet<LimitOrder> sellLimitOrders = new LimitOrderQueue(SORTING_METHOD.PRICE_ASC);
    private final SortedSet<LimitOrder> buyLimitOrders = new LimitOrderQueue(SORTING_METHOD.PRICE_DECS);
    private final SortedSet<MarketOrder> buyMarketOrders = new TreeSet<>(
        Comparator.comparingInt(ActiveOrder::getOrderId));
    private final SortedSet<MarketOrder> sellMarketOrders = new TreeSet<>(
        Comparator.comparingInt(ActiveOrder::getOrderId));

    private Optional<Float> lastExecutedTradePrice = Optional.empty();

    public Optional<Float> getLastExecutedTradePrice() {
        return lastExecutedTradePrice;
    }

    public void setLastExecutedTradePrice(float lastExecutedTradePrice) {
        this.lastExecutedTradePrice = Optional.of(lastExecutedTradePrice);
    }

    public SortedSet<LimitOrder> getSellLimitOrders() {
        return sellLimitOrders;
    }

    public SortedSet<LimitOrder> getBuyLimitOrders() {
        return buyLimitOrders;
    }

    public SortedSet<MarketOrder> getBuyMarketOrders() {
        return buyMarketOrders;
    }

    public SortedSet<MarketOrder> getSellMarketOrders() {
        return sellMarketOrders;
    }

    @Override
    public String toString() {
        return "TickerData{" +
            "sellLimitOrders=" + sellLimitOrders +
            ", buyLimitOrders=" + buyLimitOrders +
            ", buyMarketOrders=" + buyMarketOrders +
            ", sellMarketOrders=" + sellMarketOrders +
            ", lastExecutedTradePrice=" + lastExecutedTradePrice +
            '}';
    }
}
