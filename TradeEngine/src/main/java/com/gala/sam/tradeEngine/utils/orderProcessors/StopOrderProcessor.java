package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractStopOrder;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopOrderProcessor extends AbstractOrderProcessor<AbstractStopOrder> {

  private final MarketState marketState;

  public StopOrderProcessor(IOrderRepository orderRepository, ITradeRepository tradeRepository,
      MarketState marketState) {
    super(orderRepository, tradeRepository);
    this.marketState = marketState;
  }

  @Override
  public void process(AbstractStopOrder order) {
    log.debug("Adding order to stop orders: " + order.getOrderId());
    marketState.getStopOrders().add(order);
    saveOrder(order);
  }
}
