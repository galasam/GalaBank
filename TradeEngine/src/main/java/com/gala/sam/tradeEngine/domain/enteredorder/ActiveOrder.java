package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.TIME_IN_FORCE;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class ActiveOrder extends Order {

  public ActiveOrder(OrderType type, int orderId, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    super(type, orderId, clientId, direction, quantity, timeInForce, ticker);
  }

}
