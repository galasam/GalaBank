package com.gala.sam.tradeEngine.domain.dataStructures;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.ActiveOrder;

import java.util.Comparator;
import java.util.TreeSet;

public class OrderIdPriorityQueue<T extends ActiveOrder> extends TreeSet<T> {

    public OrderIdPriorityQueue() {
        super(Comparator.comparingInt(ActiveOrder::getOrderId));
    }
}
