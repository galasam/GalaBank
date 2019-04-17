package com.gala.sam.tradeEngine.service;

import static com.gala.sam.tradeEngine.utils.MarketUtils.updateMarketStateFromTradeRepository;

import com.gala.sam.tradeEngine.domain.PublicMarketStatus;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.datastructures.OrderIdPriorityQueue;
import com.gala.sam.tradeEngine.domain.datastructures.TickerData;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractActiveOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractStopOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorFactory;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.IEnteredOrderGenerator;
import com.gala.sam.tradeEngine.utils.exception.AbstractOrderFieldNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderTypeNotSupportedException;
import com.gala.sam.tradeEngine.utils.orderProcessors.AbstractOrderProcessor;
import com.gala.sam.tradeEngine.utils.orderProcessors.OrderProcessorFactory;
import com.gala.sam.tradeEngine.utils.orderValidators.IOrderValidator;
import com.gala.sam.tradeEngine.utils.orderValidators.OrderValidatorFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService {

  private final ITradeRepository tradeRepository;
  private final IOrderRepository orderRepository;
  private final EnteredOrderGeneratorFactory enteredOrderGeneratorFactory;
  private final OrderProcessorFactory orderProcessorFactory;
  private final OrderValidatorFactory orderValidatorFactory;
  private final MarketUtils marketUtils;
  private final MarketState marketState;

  @PostConstruct
  void init() {
    log.info("Getting existing trades from database");
    updateMarketStateFromTradeRepository(marketState, tradeRepository);
    marketUtils.updateMarketStateFromOrderRepository(marketState, orderRepository);
  }

  public void reset() {
    marketState.reset();
    enteredOrderGeneratorFactory.reset();
  }

  @SuppressWarnings("unchecked")
  public <T extends AbstractOrderRequest, S extends AbstractOrder> Optional<S> enterOrder(T orderRequest) {
    log.info("Processing Order Time-step with order request: {}", orderRequest);

    final S order;
    try {
      IOrderValidator<T> orderValidator = orderValidatorFactory
          .getOrderValidator(orderRequest.getType());
      List<String> errors = orderValidator.findErrors(orderRequest);
      if (!errors.isEmpty()) {
        log.error("Order request could not be validated. Reasons: {}", errors);
        return Optional.empty();
      } else {
        log.debug("Order request was validated: {}", orderRequest);
      }

      IEnteredOrderGenerator<T,S> enteredOrderGenerator = enteredOrderGeneratorFactory
          .getEnteredOrderGenerator(orderRequest.getType());

      order = enteredOrderGenerator.generateConcreteOrder(orderRequest);
      log.debug("Concrete Order generated with id: {}", order.getOrderId());

    } catch (AbstractOrderFieldNotSupportedException e) {
      log.error(
          "Concrete order could not be generated so Order Request {} will be dropped since when handling it an exception was raised: {}",
          orderRequest, e.toString());
      return Optional.empty();
    }

    tryProcessOrder(order);

    processTriggeredStopOrders();

    return Optional.of(order);
  }

  public List<Trade> getAllMatchedTrades() {
    return marketState.getTrades();
  }

  @SuppressWarnings("unchecked")
  private <T extends AbstractOrder> void tryProcessOrder(T order) {
    log.info(String.format("Processing order %s", order.toString()));

    final AbstractOrderProcessor<T> orderProcessor;
    try {
      orderProcessor = orderProcessorFactory.getOrderProcessor(order.getType());
    } catch (OrderTypeNotSupportedException e) {
      log.error(
          "Cannot create order processor so order {} will not be processed since handling it an exception was raised: {}",
          order.getOrderId(), e.toString());
      return;
    }

    handleOrderWithTimer(orderProcessor, order);

    log.debug("Ticker queues: " + marketState.getTickerQueues().toString());
    log.debug("Stop Orders: " + marketState.getStopOrders().toString());
    log.debug("Trades: " + marketState.getTrades().toString());
  }

  private <T extends AbstractOrder> void handleOrderWithTimer(AbstractOrderProcessor<T> orderProcessor, T order) {
    StopWatch orderProcessorTimer = new StopWatch();
    orderProcessorTimer.start();
    orderProcessor.process(marketState, order);
    orderProcessorTimer.stop();
    log.info("Order {} was handled in {} milliseconds", order.getOrderId(),
        orderProcessorTimer.getTotalTimeMillis());
  }

  private void processTriggeredStopOrders() {
    Iterator<AbstractStopOrder> stopOrderIterator = marketState.getStopOrders().iterator();
    while (stopOrderIterator.hasNext()) {
      AbstractStopOrder stopOrder = stopOrderIterator.next();
      log.debug("Testing Trigger on: " + stopOrder.toString());
      if (isStopLossTriggered(stopOrder)) {
        log.debug("Stop order request Triggered");
        stopOrderIterator.remove();
        orderRepository.delete(stopOrder);
        AbstractActiveOrder activeOrder = stopOrder.toActiveOrder();
        tryProcessOrder(activeOrder);
      } else {
        log.debug("Stop order request not Triggered");
      }
    }
  }

  private boolean isStopLossTriggered(AbstractStopOrder stopOrder) {
    AbstractActiveOrder activeOrder = stopOrder.toActiveOrder();
    Optional<Float> lastExec = marketState.getTickerQueueGroup(activeOrder)
        .getLastExecutedTradePrice();
    log.debug("Checking if there has been a previous trade");
    if (lastExec.isPresent()) {
      log.debug("Previous trade found, checking direction");
      if (activeOrder.getDirection().equals(Direction.BUY)) {
        log.debug("Buy direction: testing trigger");
        return stopOrder.getTriggerPrice() <= lastExec.get();
      } else if (activeOrder.getDirection().equals(Direction.SELL)) {
        log.debug("Sell direction: testing trigger");
        return stopOrder.getTriggerPrice() >= lastExec.get();
      } else {
        log.error("Stop Order {} has unsupported direction {} so cannot be triggered",
            stopOrder.getOrderId(), stopOrder.getDirection());
        return false;
      }
    } else {
      log.debug("No previous trade found");
      return false;
    }
  }

  public PublicMarketStatus getStatus() {
    class TickerProcessorHelper {

      private List<PublicMarketStatus.Ticker> tickers = new ArrayList<>();

      private void processTicker(String name, TickerData data) {
        SortedSet<AbstractActiveOrder> buyOrders = getActiveOrders(data.getBuyLimitOrders(),
            data.getBuyMarketOrders());
        SortedSet<AbstractActiveOrder> sellOrders = getActiveOrders(data.getSellLimitOrders(),
            data.getSellMarketOrders());

        if (!(buyOrders.isEmpty() && sellOrders.isEmpty())) {
          tickers.add(PublicMarketStatus.Ticker.builder()
              .name(name)
              .buy(new ArrayList<>(buyOrders))
              .sell(new ArrayList<>(sellOrders))
              .build());
        }
      }

      private SortedSet<AbstractActiveOrder> getActiveOrders(SortedSet<LimitOrder> buyLimitOrders,
          SortedSet<MarketOrder> buyMarketOrders) {
        SortedSet<AbstractActiveOrder> buyOrders = new OrderIdPriorityQueue<>();
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
