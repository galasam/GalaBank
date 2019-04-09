package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.OrderTypeNotSupportedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderProcessorFactory {

  private final ITradeRepository tradeRepository;
  private final IOrderRepository orderRepository;
  private final MarketUtils marketUtils;
  private final OrderProcessorUtils orderProcessorUtils;

  public AbstractOrderProcessor getOrderProcessor(MarketState marketState, OrderType type)
      throws OrderTypeNotSupportedException {
    switch (type) {
      case STOP_LIMIT:
      case STOP_MARKET:
        return new StopOrderProcessor(orderRepository, tradeRepository, marketState, marketUtils);
      case ACTIVE_LIMIT:
        return new ActiveLimitOrderProcessor(orderRepository, tradeRepository, marketState,
            marketUtils, orderProcessorUtils);
      case ACTIVE_MARKET:
        return new ActiveMarketOrderProcessor(orderRepository, tradeRepository, marketState,
            marketUtils, orderProcessorUtils);
      default:
        log.error("Order type {} is not supported so cannot create order processor", type);
        throw new OrderTypeNotSupportedException(type);
    }
  }
}
