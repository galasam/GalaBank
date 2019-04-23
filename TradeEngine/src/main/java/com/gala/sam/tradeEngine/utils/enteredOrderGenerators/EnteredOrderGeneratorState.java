package com.gala.sam.tradeEngine.utils.enteredOrderGenerators;

import org.springframework.stereotype.Component;

@Component
public class EnteredOrderGeneratorState {

  private static final int INITIAL_ORDER_INDEX = 1;
  private int currentOrderIndex = INITIAL_ORDER_INDEX;

  private void incrementOrderIndex() {
    currentOrderIndex++;
  }

  public int getNextOrderId() {
    int nextOrderId = currentOrderIndex;
    incrementOrderIndex();
    return nextOrderId;
  }

  public void reset() {
    currentOrderIndex = INITIAL_ORDER_INDEX;
  }
}
