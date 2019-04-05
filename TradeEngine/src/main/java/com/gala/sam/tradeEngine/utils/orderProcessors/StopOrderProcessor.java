package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractStopOrder;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopOrderProcessor extends AbstractOrderProcessor {

  public StopOrderProcessor(IOrderRepository orderRepository, ITradeRepository tradeRepository,
      MarketState marketState, MarketUtils marketUtils) {
    super(orderRepository, tradeRepository, marketState, marketUtils);
  }

  @Override
  public <T extends AbstractOrder> void process(T order) {
    log.debug("Adding order to stop orders: " + order.getOrderId());
    marketState.getStopOrders().add((AbstractStopOrder) order);
    saveOrderToDatabase(order);
  }
}
