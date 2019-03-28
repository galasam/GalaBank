package com.gala.sam.tradeEngine.service;

import static com.gala.sam.tradeEngine.utils.MarketUtils.updateMarketStateFromOrderRepository;
import static com.gala.sam.tradeEngine.utils.MarketUtils.updateMarketStateFromTradeRepository;

import com.gala.sam.tradeEngine.domain.EnteredOrder.ActiveOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.LimitOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.MarketOrder;
import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import com.gala.sam.tradeEngine.domain.EnteredOrder.StopOrder;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.DIRECTION;
import com.gala.sam.tradeEngine.domain.PublicMarketStatus;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.domain.dataStructures.OrderIdPriorityQueue;
import com.gala.sam.tradeEngine.domain.dataStructures.TickerData;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MarketService {


  private final ConcreteOrderGenerator concreteOrderGenerator;
  private final TradeRepository tradeRepository;
  private final OrderRepository orderRepository;
  private final OrderProcessorFactory orderProcessorFactory;
  private MarketState marketState = new MarketState();

  public MarketService(TradeRepository tradeRepository,
      OrderRepository orderRepository,
      ConcreteOrderGenerator concreteOrderGenerator,
      OrderProcessorFactory orderProcessorFactory) {
    this.tradeRepository = tradeRepository;
    this.orderRepository = orderRepository;
    this.concreteOrderGenerator = concreteOrderGenerator;
    this.orderProcessorFactory = orderProcessorFactory;
  }

  @PostConstruct
  void init() {
    log.info("Getting existing trades from database");
    updateMarketStateFromTradeRepository(marketState, tradeRepository);
    updateMarketStateFromOrderRepository(marketState, orderRepository);
  }

  public Order enterOrder(OrderReq orderReq) {
    log.info("Processing Triggered Stop Orders");

    Order order = concreteOrderGenerator.getConcreteOrder(orderReq);

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
        log.info("Stop OrderReq Triggered");
        it.remove();
        orderRepository.delete(stopOrder);
        ActiveOrder activeOrder = stopOrder.toActiveOrder();
        processOrder(activeOrder);
      } else {
        log.info("Stop OrderReq not Triggered");
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
        throw new UnsupportedOperationException("OrderReq direction not supported");
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
