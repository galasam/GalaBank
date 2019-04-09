package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractOrderProcessor<T extends AbstractOrder> {

  public final static boolean SUCCESS = true;
  public final static boolean FAILURE = false;

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

  public abstract void process(T order);

}
