package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderProcessorFactory {

  private final ITradeRepository tradeRepository;
  private final IOrderRepository orderRepository;
  private final MarketUtils marketUtils;

  public AbstractOrderProcessor getOrderProcessor(MarketState marketState, OrderType type) {
    switch (type) {
      case STOP_LIMIT:
      case STOP_MARKET:
        return new StopOrderProcessor(orderRepository, tradeRepository, marketState, marketUtils);
      case ACTIVE_LIMIT:
        return new ActiveLimitOrderProcessor(orderRepository, tradeRepository, marketState, marketUtils);
      case ACTIVE_MARKET:
        return new ActiveMarketOrderProcessor(orderRepository, tradeRepository, marketState, marketUtils);
      default:
        throw new UnsupportedOperationException("OrderReq type not specified");
    }
  }
}
