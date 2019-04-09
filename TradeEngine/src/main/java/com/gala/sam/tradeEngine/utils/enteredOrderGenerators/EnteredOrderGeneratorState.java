package com.gala.sam.tradeEngine.utils.enteredOrderGenerators;

import org.springframework.stereotype.Component;

@Component
public class EnteredOrderGeneratorState {

  private static final int InitialOrderIndex = 1;
  private int currentOrderIndex = InitialOrderIndex;

  private void incrementOrderIndex() {
    currentOrderIndex++;
  }

  public int getNextOrderId() {
    int nextOrderId = currentOrderIndex;
    incrementOrderIndex();
    return nextOrderId;
  }

}
