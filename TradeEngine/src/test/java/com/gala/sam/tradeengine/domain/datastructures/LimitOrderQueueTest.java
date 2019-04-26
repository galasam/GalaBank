package com.gala.sam.tradeengine.domain.datastructures;

import static org.junit.Assert.*;

import com.gala.sam.tradeengine.domain.datastructures.LimitOrderQueue.SortingMethod;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

public class LimitOrderQueueTest {

  @Test
  public void addLargerElementToPRICE_ASCQueue() {
    //Given: second element with larger limit than first and PRICE_ASC Queue
    TwoLimitOrders orders = getTwoLimitOrdersOfDifferentLimitAmount();
    LimitOrderQueue queue = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    //When: entered in to price queue smaller then bigger
    queue.add(orders.smallerLimit);
    queue.add(orders.largerLimit);
    //Then: smaller comes out first
    Assert.assertEquals("Smaller should come out first", orders.smallerLimit, queue.iterator().next());
  }

  @Test
  public void addSmallerElementToPRICE_ASCQueue() {
    //Given: second element with larger limit than first and PRICE_ASC Queue
    TwoLimitOrders orders = getTwoLimitOrdersOfDifferentLimitAmount();
    LimitOrderQueue queue = new LimitOrderQueue(SortingMethod.PRICE_ASC);
    //When: entered in to price queue smaller then bigger
    queue.add(orders.largerLimit);
    queue.add(orders.smallerLimit);
    //Then: smaller comes out first
    Assert.assertEquals("Smaller should come out first", orders.smallerLimit, queue.iterator().next());
  }

  @Test
  public void addLargerElementToPRICE_DECSQueue() {
    //Given: second element with larger limit than first and PRICE_ASC Queue
    TwoLimitOrders orders = getTwoLimitOrdersOfDifferentLimitAmount();
    LimitOrderQueue queue = new LimitOrderQueue(SortingMethod.PRICE_DECS);
    //When: entered in to price queue smaller then bigger
    queue.add(orders.largerLimit);
    queue.add(orders.smallerLimit);
    //Then: smaller comes out first
    Assert.assertEquals("Larger should come out first", orders.largerLimit, queue.iterator().next());
  }

  @Test
  public void addSmallerElementToPRICE_DECSQueue() {
    //Given: second element with larger limit than first and PRICE_ASC Queue
    TwoLimitOrders orders = getTwoLimitOrdersOfDifferentLimitAmount();
    LimitOrderQueue queue = new LimitOrderQueue(SortingMethod.PRICE_DECS);
    //When: entered in to price queue smaller then bigger
    queue.add(orders.smallerLimit);
    queue.add(orders.largerLimit);
    //Then: smaller comes out first
    Assert.assertEquals("Larger should come out first", orders.largerLimit, queue.iterator().next());
  }

  private TwoLimitOrders getTwoLimitOrdersOfDifferentLimitAmount() {
    return new TwoLimitOrders(
        LimitOrder.builder().limit(1.0f).build(),
        LimitOrder.builder().limit(2.0f).build()
    );
  }

  @Getter
  @RequiredArgsConstructor
  static class TwoLimitOrders {
    final LimitOrder smallerLimit;
    final LimitOrder largerLimit;
  }

}