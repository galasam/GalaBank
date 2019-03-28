package com.gala.sam.tradeEngine.domain.OrderReq;

import lombok.Data;
import lombok.experimental.NonFinal;

@Data
@NonFinal
public abstract class Order {

  final OrderType type;
  int clientId;
  DIRECTION direction;
  int quantity;
  int quantityRemaining;
  TIME_IN_FORCE timeInForce;
  String ticker;

  public Order(OrderType type, int clientId, DIRECTION direction, int quantity,
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

  public abstract com.gala.sam.tradeEngine.domain.ConcreteOrder.Order toConcrete(int orderId);

  public enum OrderType {STOP, ACTIVE_LIMIT, ACTIVE_MARKET}

  public enum DIRECTION {SELL, BUY}

  public enum TIME_IN_FORCE {FOK, GTC}
}
