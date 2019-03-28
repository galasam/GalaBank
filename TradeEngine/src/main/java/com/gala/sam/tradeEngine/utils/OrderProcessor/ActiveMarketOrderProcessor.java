package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.LimitOrder;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.MarketOrder;
import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;

import static com.gala.sam.tradeEngine.utils.MarketUtils.makeTrade;
import static com.gala.sam.tradeEngine.utils.MarketUtils.queueIfTimeInForce;

@Slf4j
public class ActiveMarketOrderProcessor extends OrderProcessor {

    private final MarketState marketState;

    public ActiveMarketOrderProcessor(OrderRepository orderRepository, TradeRepository tradeRepository, MarketState marketState) {
        super(orderRepository, tradeRepository);
        this.marketState = marketState;
    }

    @Override
    public <T extends Order> void process(T order) {
        processMarketOrder((MarketOrder) order);
    }

    private void processMarketOrder(MarketOrder marketOrder) {
        TickerData tickerData = marketState.getTickerQueueGroup(marketOrder);
        if (marketOrder.getDirection() == DIRECTION.BUY) {
            processDirectedMarketOrder(marketOrder, tickerData,
                    tickerData.getSellLimitOrders(),tickerData.getBuyMarketOrders());
        } else if (marketOrder.getDirection() == DIRECTION.SELL) {
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
            queueIfTimeInForce(marketOrder, marketOrders, this::saveOrder);
        } else {
            LimitOrder limitOrder = limitOrders.first();
            log.debug("Limit Order queue not empty, so trading with best limit order: " + limitOrder.toString());
            makeTrade(marketState, marketOrder, limitOrder, limitOrder.getLimit(), tickerData, this::saveTrade);
            log.debug("Removing limit order if it is fully satisfied.");
            if (limitOrder.isFullyFulfilled()) {
                limitOrders.remove(limitOrder);
                deleteOrder(limitOrder);
            }
            log.debug("If new market order is not fully satisfied, continue processing it.");
            if (!marketOrder.isFullyFulfilled()) {
                processDirectedMarketOrder(marketOrder, tickerData, limitOrders, marketOrders);
            }
        }
    }

}
