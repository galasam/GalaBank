package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.repository.OrderRepository;
import com.gala.sam.tradeEngine.repository.TradeRepository;

public abstract class OrderProcessor {

    private OrderRepository orderRepository;
    private TradeRepository tradeRepository;

    public OrderProcessor(OrderRepository orderRepository, TradeRepository tradeRepository) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

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
