package com.gala.sam.tradeEngine.domain.enteredorder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.utils.AbstractActiveOrderDeserializer;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonDeserialize(using = AbstractActiveOrderDeserializer.class)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractActiveOrder extends AbstractOrder {

  public AbstractActiveOrder(OrderType type, int orderId, int clientId, Direction direction,
      int quantity,
      TimeInForce timeInForce, String ticker) {
    super(type, orderId, clientId, direction, quantity, timeInForce, ticker);
  }

}
