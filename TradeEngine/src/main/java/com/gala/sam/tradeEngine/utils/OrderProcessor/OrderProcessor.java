package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.enteredorder.Order;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class OrderProcessor {

  private final OrderRepository orderRepository;
  private final TradeRepository tradeRepository;

  protected void saveOrder(Order order) {
    orderRepository.save(order);
  }

  protected void deleteOrder(Order order) {
    orderRepository.delete(order);
  }

  protected void saveTrade(Trade order) {
    tradeRepository.save(order);
  }

  public abstract <T extends Order> void process(T order);

}
