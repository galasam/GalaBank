package com.gala.sam.tradeEngine.domain.orderrequest;

import lombok.Data;
import lombok.experimental.NonFinal;

@Data
@NonFinal
public abstract class OrderRequest {

  final OrderType type;
  int clientId;
  DIRECTION direction;
  int quantity;
  int quantityRemaining;
  TIME_IN_FORCE timeInForce;
  String ticker;

  public OrderRequest(OrderType type, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    this.type = type;
    this.clientId = clientId;
    this.direction = direction;
    this.quantity = quantity;
    this.quantityRemaining = this.quantity;
    this.timeInForce = timeInForce;
    this.ticker = ticker;
  }

  public boolean isFullyFulfilled() {
    return quantityRemaining == 0;
  }

  public void reduceQuantityRemaining(int reduction) {
    if (reduction > quantityRemaining) {
      throw new IllegalArgumentException("Reduction: " + Integer.toString(reduction)
          + " larger than remaining quantity: " + Integer.toString(quantityRemaining));
    } else {
      quantityRemaining -= reduction;
    }
  }

  public abstract com.gala.sam.tradeEngine.domain.enteredorder.Order toConcrete(int orderId);

  public enum OrderType {STOP, ACTIVE_LIMIT, ACTIVE_MARKET}

  public enum DIRECTION {SELL, BUY}

  public enum TIME_IN_FORCE {FOK, GTC}
}
