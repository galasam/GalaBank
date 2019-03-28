package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.OrderType;
import com.gala.sam.tradeEngine.domain.dataStructures.MarketState;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderProcessorFactory {

  private final TradeRepository tradeRepository;
  private final OrderRepository orderRepository;

  public OrderProcessor getOrderProcessor(MarketState marketState, OrderType type) {
    switch (type) {
      case STOP:
        return new StopOrderProcessor(orderRepository, tradeRepository, marketState);
      case ACTIVE_LIMIT:
        return new ActiveLimitOrderProcessor(orderRepository, tradeRepository, marketState);
      case ACTIVE_MARKET:
        return new ActiveMarketOrderProcessor(orderRepository, tradeRepository, marketState);
      default:
        throw new UnsupportedOperationException("OrderReq type not specified");
    }
  }
}
