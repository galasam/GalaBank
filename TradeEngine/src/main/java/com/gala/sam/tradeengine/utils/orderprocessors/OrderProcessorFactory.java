package com.gala.sam.tradeengine.utils.orderprocessors;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeengine.utils.exception.OrderTypeNotSupportedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProcessorFactory {

  private final StopOrderProcessor stopOrderProcessor;
  private final ActiveLimitOrderProcessor activeLimitOrderProcessor;
  private final ActiveMarketOrderProcessor activeMarketOrderProcessor;

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
