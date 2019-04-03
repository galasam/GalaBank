package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class AbstractActiveOrder extends AbstractOrder {

  public AbstractActiveOrder(OrderType type, int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker) {
    super(type, orderId, clientId, direction, quantity, timeInForce, ticker);
  }

}
