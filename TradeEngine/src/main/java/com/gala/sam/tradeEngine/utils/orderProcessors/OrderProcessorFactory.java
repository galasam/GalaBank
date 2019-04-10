package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.OrderTypeNotSupportedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderProcessorFactory {

  @Autowired
  StopOrderProcessor stopOrderProcessor;
  @Autowired
  ActiveLimitOrderProcessor activeLimitOrderProcessor;
  @Autowired
  ActiveMarketOrderProcessor activeMarketOrderProcessor;

  public AbstractOrderProcessor getOrderProcessor(OrderType type)
      throws OrderTypeNotSupportedException {
    switch (type) {
      case STOP_LIMIT:
      case STOP_MARKET:
        return stopOrderProcessor;
      case ACTIVE_LIMIT:
        return activeLimitOrderProcessor;
      case ACTIVE_MARKET:
        return activeMarketOrderProcessor;
      default:
        log.error("Order type {} is not supported so cannot create order processor", type);
        throw new OrderTypeNotSupportedException(type);
    }
  }
}
