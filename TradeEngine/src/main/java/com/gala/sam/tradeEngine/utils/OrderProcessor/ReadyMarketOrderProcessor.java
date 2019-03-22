package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.LimitOrder;
import com.gala.sam.tradeEngine.domain.MarketOrder;
import com.gala.sam.tradeEngine.domain.Order;
import com.gala.sam.tradeEngine.domain.ReadyOrder;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;

@AllArgsConstructor
@Slf4j
public class ReadyMarketOrderProcessor implements OrderProcessor {

    final MarketState marketState;

    @Override
    public <T extends Order> void process(T order) {
        processMarketOrder((MarketOrder) order);
    }

    private void processMarketOrder(MarketOrder marketOrder) {
        TickerData tickerData = marketState.getTickerQueueGroup(marketOrder);
        if (marketOrder.getDirection() == ReadyOrder.DIRECTION.BUY) {
            processDirectedMarketOrder(marketOrder, tickerData,
                    tickerData.getSellLimitOrders(),tickerData.getBuyMarketOrders());
        } else if (marketOrder.getDirection() == ReadyOrder.DIRECTION.SELL) {
            processDirectedMarketOrder(marketOrder, tickerData,
                    tickerData.getBuyLimitOrders(), tickerData.getSellMarketOrders());
        } else {
            throw new UnsupportedOperationException("Order direction not supported");
        }
    }

    private void processDirectedMarketOrder(MarketOrder marketOrder, TickerData tickerData,
                                            SortedSet<LimitOrder> limitOrders, SortedSet<MarketOrder> marketOrders) {
        log.debug("Checking Limit Order queue");
        if(limitOrders.isEmpty()) {
            log.debug("Limit Order queue empty, so check if time in force");
            queueIfTimeInForce(marketOrder, marketOrders);
        } else {
            LimitOrder limitOrder = limitOrders.first();
            log.debug("Limit Order queue not empty, so trading with best limit order: " + limitOrder.toString());
            limitOrders.remove(limitOrder);
            makeTrade(marketState, marketOrder, limitOrder, limitOrder.getLimit(), tickerData);
        }
    }

}
