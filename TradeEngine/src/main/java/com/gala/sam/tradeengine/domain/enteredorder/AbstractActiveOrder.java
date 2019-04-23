package com.gala.sam.tradeengine.domain.enteredorder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeengine.utils.AbstractActiveOrderDeserializer;
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
