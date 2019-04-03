package com.gala.sam.tradeEngine.service;

import static com.gala.sam.tradeEngine.utils.MarketUtils.updateMarketStateFromOrderRepository;
import static com.gala.sam.tradeEngine.utils.MarketUtils.updateMarketStateFromTradeRepository;

import com.gala.sam.tradeEngine.domain.enteredorder.ActiveOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.Order;
import com.gala.sam.tradeEngine.domain.enteredorder.StopOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.PublicMarketStatus;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.OrderIdPriorityQueue;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import com.gala.sam.tradeEngine.utils.ConcreteOrderGenerator;
import com.gala.sam.tradeEngine.utils.OrderProcessor.OrderProcessorFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService {


  private final TradeRepository tradeRepository;
  private final OrderRepository orderRepository;
  private final ConcreteOrderGenerator concreteOrderGenerator;
  private final OrderProcessorFactory orderProcessorFactory;
  private MarketState marketState = new MarketState();

  @PostConstruct
  void init() {
    log.info("Getting existing trades from database");
    updateMarketStateFromTradeRepository(marketState, tradeRepository);
    updateMarketStateFromOrderRepository(marketState, orderRepository);
  }

  public Order enterOrder(OrderRequest orderRequest) {
    log.info("Processing Triggered Stop Orders");

    Order order = concreteOrderGenerator.getConcreteOrder(orderRequest);

    processOrder(order);
    processTriggeredStopOrders();
    return order;
  }

  public List<Trade> getAllMatchedTrades() {
    return marketState.getTrades();
  }

  private void processOrder(Order order) {
    log.info(String.format("Processing order %s", order.toString()));

    orderProcessorFactory.getOrderProcessor(marketState, order.getType())
        .process(order);

    log.info("Ticker queues: " + marketState.getTickerQueues().toString());
    log.info("Stop Orders: " + marketState.getStopOrders().toString());
    log.info("Trades: " + marketState.getTrades().toString());
  }

  private void processTriggeredStopOrders() {
    Iterator<StopOrder> it = marketState.getStopOrders().iterator();
    while (it.hasNext()) {
      StopOrder stopOrder = it.next();
      log.info("Testing Trigger on: " + stopOrder.toString());
      if (isStopLossTriggered(stopOrder)) {
        log.info("Stop orderrequest Triggered");
        it.remove();
        orderRepository.delete(stopOrder);
        ActiveOrder activeOrder = stopOrder.toActiveOrder();
        processOrder(activeOrder);
      } else {
        log.info("Stop orderrequest not Triggered");
      }
    }
  }

  private boolean isStopLossTriggered(StopOrder stopOrder) {
    ActiveOrder activeOrder = stopOrder.toActiveOrder();
    Optional<Float> lastExec = marketState.getTickerQueueGroup(activeOrder)
        .getLastExecutedTradePrice();
    log.debug("Checking if there has been a previous trade");
    if (lastExec.isPresent()) {
      log.debug("Previous trade found, checking direction");
      if (activeOrder.getDirection().equals(DIRECTION.BUY)) {
        log.debug("Buy direction: testing trigger");
        return stopOrder.getTriggerPrice() <= lastExec.get();
      } else if (activeOrder.getDirection().equals(DIRECTION.SELL)) {
        log.debug("Sell direction: testing trigger");
        return stopOrder.getTriggerPrice() >= lastExec.get();
      } else {
        throw new UnsupportedOperationException("orderrequest direction not supported");
      }
    } else {
      log.debug("No previous trade found");
      return false;
    }
  }

  public PublicMarketStatus getStatus() {
    class TickerProcessorHelper {

      List<PublicMarketStatus.Ticker> tickers = new ArrayList<>();

      void processTicker(String name, TickerData data) {
        SortedSet<ActiveOrder> buyOrders = getActiveOrders(data.getBuyLimitOrders(),
            data.getBuyMarketOrders());
        SortedSet<ActiveOrder> sellOrders = getActiveOrders(data.getSellLimitOrders(),
            data.getSellMarketOrders());

        if (!(buyOrders.isEmpty() && sellOrders.isEmpty())) {
          tickers.add(PublicMarketStatus.Ticker.builder()
              .name(name)
              .buy(new ArrayList<>(buyOrders))
              .sell(new ArrayList<>(sellOrders))
              .build());
        }
      }

      private SortedSet<ActiveOrder> getActiveOrders(SortedSet<LimitOrder> buyLimitOrders,
          SortedSet<MarketOrder> buyMarketOrders) {
        SortedSet<ActiveOrder> buyOrders = new OrderIdPriorityQueue<>();
        buyOrders.addAll(buyLimitOrders);
        buyOrders.addAll(buyMarketOrders);
        return buyOrders;
      }
    }
    TickerProcessorHelper h = new TickerProcessorHelper();
    marketState.getTickerQueues().forEach(h::processTicker);
    return PublicMarketStatus.builder()
        .trades(getAllMatchedTrades())
        .orders(h.tickers)
        .build();
  }
}
