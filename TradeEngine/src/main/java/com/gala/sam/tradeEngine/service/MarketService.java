package com.gala.sam.tradeEngine.service;

import static com.gala.sam.tradeEngine.utils.MarketUtils.updateMarketStateFromOrderRepository;
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
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.EnteredOrderGeneratorFactory;
import com.gala.sam.tradeEngine.utils.enteredOrderGenerators.IEnteredOrderGenerator;
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
  private final EnteredOrderGeneratorFactory concreteOrderGeneratorFactory;
  private final OrderProcessorFactory orderProcessorFactory;
  private final OrderValidatorFactory orderValidatorFactory;
  private MarketState marketState = new MarketState();

  @PostConstruct
  void init() {
    log.info("Getting existing trades from database");
    updateMarketStateFromTradeRepository(marketState, tradeRepository);
    updateMarketStateFromOrderRepository(marketState, orderRepository);
  }

  public Optional<AbstractOrder> enterOrder(AbstractOrderRequest orderRequest) {
    log.info("Processing Order Time-step with order request: {}", orderRequest);

    IOrderValidator<AbstractOrderRequest> orderValidator = orderValidatorFactory
        .getOrderValidator(orderRequest.getType());
    List<String> errors = orderValidator.findErrors(orderRequest);
    if (!errors.isEmpty()) {
      log.error("Order request could not be validated. Reasons: {}", errors);
      return Optional.empty();
    } else {
      log.debug("Order request was validated: {}", orderRequest);
    }

    IEnteredOrderGenerator enteredOrderGenerator = concreteOrderGeneratorFactory
        .getEnteredOrderGenerator(orderRequest.getType());
    AbstractOrder order = enteredOrderGenerator.generateConcreteOrder(orderRequest);
    log.debug("Concrete Order generated with id: {}", order.getOrderId());

    processOrder(order);
    processTriggeredStopOrders();
    return Optional.of(order);
  }

  public List<Trade> getAllMatchedTrades() {
    return marketState.getTrades();
  }

  private void processOrder(AbstractOrder order) {
    log.info(String.format("Processing order %s", order.toString()));

    AbstractOrderProcessor orderProcessor = orderProcessorFactory
        .getOrderProcessor(marketState, order.getType());
    withTimer(() -> orderProcessor.process(order), order.getOrderId());

    log.debug("Ticker queues: " + marketState.getTickerQueues().toString());
    log.debug("Stop Orders: " + marketState.getStopOrders().toString());
    log.debug("Trades: " + marketState.getTrades().toString());
  }

  private void withTimer(Runnable f, int orderId) {
    StopWatch orderProcessorTimer = new StopWatch();
    orderProcessorTimer.start();
    f.run();
    orderProcessorTimer.stop();
    log.info("Order {} was processed in {} milliseconds", orderId,
        orderProcessorTimer.getTotalTimeMillis());
  }

  private void processTriggeredStopOrders() {
    Iterator<AbstractStopOrder> it = marketState.getStopOrders().iterator();
    while (it.hasNext()) {
      AbstractStopOrder stopOrder = it.next();
      log.debug("Testing Trigger on: " + stopOrder.toString());
      if (isStopLossTriggered(stopOrder)) {
        log.debug("Stop order request Triggered");
        it.remove();
        orderRepository.delete(stopOrder);
        AbstractActiveOrder activeOrder = stopOrder.toActiveOrder();
        processOrder(activeOrder);
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
