package com.gala.sam.tradeengine.utils.orderprocessors;

import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.domain.datastructures.MarketState;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeengine.repository.IOrderRepository;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import com.gala.sam.tradeengine.utils.MarketUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractOrderProcessor<T extends AbstractOrder> {

  protected final MarketUtils marketUtils;
  private final IOrderRepository orderRepository;
  private final ITradeRepository tradeRepository;
  protected final PersistenceHelper persistenceHelper = new PersistenceHelper();

  public abstract void process(MarketState marketState, T order);

  class PersistenceHelper {

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
  }

}
