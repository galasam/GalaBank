package com.gala.sam.tradeEngine.domain.orderrequest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gala.sam.tradeEngine.utils.OrderRequestDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;

@Data
@NonFinal
@NoArgsConstructor
@JsonDeserialize(using = OrderRequestDeserializer.class)
public abstract class AbstractOrderRequest {

  OrderType type;
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

  public enum OrderType {STOP_LIMIT, STOP_MARKET, ACTIVE_LIMIT, ACTIVE_MARKET}

  public enum Direction {SELL, BUY}

  public enum TimeInForce {FOK, GTC}
}
