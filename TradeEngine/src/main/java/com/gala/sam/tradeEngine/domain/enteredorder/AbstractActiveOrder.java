package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TIME_IN_FORCE;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class AbstractActiveOrder extends AbstractOrder {

  public AbstractActiveOrder(OrderType type, int orderId, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    super(type, orderId, clientId, direction, quantity, timeInForce, ticker);
  }

}
