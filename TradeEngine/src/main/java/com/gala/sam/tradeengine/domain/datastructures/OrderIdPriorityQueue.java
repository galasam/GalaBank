package com.gala.sam.tradeengine.domain.datastructures;

import com.gala.sam.tradeengine.domain.enteredorder.AbstractActiveOrder;
import java.util.Comparator;
import java.util.TreeSet;

public class OrderIdPriorityQueue<T extends AbstractActiveOrder> extends TreeSet<T> {

  public OrderIdPriorityQueue() {
    super(Comparator.comparingInt(AbstractActiveOrder::getOrderId));
  }
}
