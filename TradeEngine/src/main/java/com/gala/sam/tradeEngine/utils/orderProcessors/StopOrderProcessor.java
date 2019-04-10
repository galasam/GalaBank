package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractStopOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StopOrderProcessor extends AbstractOrderProcessor<AbstractStopOrder> {

  @Override
  public void process(MarketState marketState, AbstractStopOrder order) {
    log.debug("Adding order to stop orders: " + order.getOrderId());
    marketState.getStopOrders().add(order);
    saveOrderToDatabase(order);
  }

}
