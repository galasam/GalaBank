package com.gala.sam.tradeengine.utils.orderprocessors;

import com.gala.sam.tradeengine.domain.datastructures.MarketState;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractStopOrder;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.utils.MarketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StopOrderProcessor extends AbstractOrderProcessor<AbstractStopOrder> {

  public StopOrderProcessor(MarketUtils marketUtils,
      IOrderRepository orderRepository,
      ITradeRepository tradeRepository) {
    super(marketUtils, orderRepository, tradeRepository);
  }

  @Override
  public void process(MarketState marketState, AbstractStopOrder order) {
    log.debug("Adding order to stop orders: " + order.getOrderId());
    marketState.getStopOrders().add(order);
    saveOrderToDatabase(order);
  }

}
