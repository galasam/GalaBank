package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class OrderProcessor {

  private final IOrderRepository orderRepository;
  private final ITradeRepository tradeRepository;

  protected void saveOrder(AbstractOrder order) {
    orderRepository.save(order);
  }

  protected void deleteOrder(AbstractOrder order) {
    orderRepository.delete(order);
  }

  protected void saveTrade(Trade order) {
    tradeRepository.save(order);
  }

  public abstract <T extends AbstractOrder> void process(T order);

}
