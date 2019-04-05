package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import com.gala.sam.tradeEngine.utils.exception.OrderTimeInForceNotSupportedException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractOrderProcessor {

  private final IOrderRepository orderRepository;
  private final ITradeRepository tradeRepository;
  protected final MarketState marketState;
  protected final MarketUtils marketUtils;

  protected void saveOrderToDatabase(AbstractOrder order) {
    orderRepository.save(order);
  }

  protected void deleteOrderFromDatabase(AbstractOrder order) {
    orderRepository.delete(order);
  }

  protected void saveTradeToDatabase(Trade trade) {
    tradeRepository.save(trade);
  }

  protected void addTradeToState(Trade trade) {
    marketState.getTrades().add(trade);
  }

  protected void addTradeToStateAndPersist(Trade trade) {
    addTradeToState(trade);
    saveTradeToDatabase(trade);
  }

  public abstract <T extends AbstractOrder> void process(T order)
      throws OrderTimeInForceNotSupportedException, OrderDirectionNotSupportedException;

}
