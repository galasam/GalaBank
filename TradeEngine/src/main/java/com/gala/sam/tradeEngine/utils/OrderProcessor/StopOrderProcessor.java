package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import com.gala.sam.tradeEngine.domain.EnteredOrder.StopOrder;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopOrderProcessor extends OrderProcessor {

  private final MarketState marketState;

  public StopOrderProcessor(OrderRepository orderRepository, TradeRepository tradeRepository,
      MarketState marketState) {
    super(orderRepository, tradeRepository);
    this.marketState = marketState;
  }

  @Override
  public <T extends Order> void process(T order) {
    log.debug("Adding stop order: " + order.toString());
    marketState.getStopOrders().add((StopOrder) order);
    saveOrder(order);
  }
}
