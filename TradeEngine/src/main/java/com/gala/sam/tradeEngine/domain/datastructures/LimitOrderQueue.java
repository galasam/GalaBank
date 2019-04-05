package com.gala.sam.tradeEngine.domain.datastructures;


import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import java.util.Comparator;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LimitOrderQueue extends TreeSet<LimitOrder> {

  private static final Comparator<LimitOrder> price_asc = (LimitOrder a, LimitOrder b) -> {
    float comp = a.getLimit() - b.getLimit();
    if (comp == 0) {
      return a.getOrderId() - b.getOrderId();
    } else if (comp < 0) {
      return -1;
    } else {
      return 1;
    }
  };
  private static final Comparator<LimitOrder> price_desc = (a, b) -> {
    float comp = b.getLimit() - a.getLimit();
    if (comp == 0) {
      return a.getOrderId() - b.getOrderId();
    } else if (comp < 0) {
      return -1;
    } else {
      return 1;
    }
  };

  public LimitOrderQueue(SortingMethod method) {
    super(getComparator(method));
  }

  private static Comparator<LimitOrder> getComparator(SortingMethod method) {
    switch (method) {
      case PRICE_ASC:
        return price_asc;
      case PRICE_DECS:
        return price_desc;
      default:
        log.error("Sorting method is not supported so cannot create comparator");
        throw new UnsupportedOperationException("Sorting method not supported");
    }
  }

  public enum SortingMethod {PRICE_ASC, PRICE_DECS}

}
