package com.gala.sam.tradeEngine.utils.orderProcessors;

import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.datastructures.MarketState;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.repository.IOrderRepository;
import com.gala.sam.tradeEngine.repository.ITradeRepository;
import com.gala.sam.tradeEngine.utils.MarketUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractOrderProcessor<T extends AbstractOrder> {

  @Autowired
  protected MarketUtils marketUtils;
  @Autowired
  private IOrderRepository orderRepository;
  @Autowired
  private ITradeRepository tradeRepository;

  protected void saveOrderToDatabase(AbstractOrder order) {
    orderRepository.save(order);
  }

  protected void deleteOrderFromDatabase(AbstractOrder order) {
    orderRepository.delete(order);
  }

  protected void saveTradeToDatabase(Trade trade) {
    tradeRepository.save(trade);
  }

  protected void addTradeToState(List<Trade> trades, Trade trade) {
    trades.add(trade);
  }

  protected void addTradeToStateAndPersist(List<Trade> trades, Trade trade) {
    addTradeToState(trades, trade);
    saveTradeToDatabase(trade);
  }

  public abstract void process(MarketState marketState, T order);

}
