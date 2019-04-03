package com.gala.sam.tradeEngine.domain.orderrequest;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import lombok.Data;
import lombok.experimental.NonFinal;

@Data
@NonFinal
public abstract class AbstractOrderRequest {

  final OrderType type;
  int clientId;
  Direction direction;
  int quantity;
  int quantityRemaining;
  TimeInForce timeInForce;
  String ticker;

  public AbstractOrderRequest(OrderType type, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker) {
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

  public abstract AbstractOrder toConcrete(int orderId);

  public enum OrderType {STOP, ACTIVE_LIMIT, ACTIVE_MARKET}

  public enum Direction {SELL, BUY}

  public enum TimeInForce {FOK, GTC}
}
