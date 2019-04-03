package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TIME_IN_FORCE;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(name = "MarketOrderRequest")
@DiscriminatorValue("MarketOrderRequest")
public class MarketOrder extends AbstractActiveOrder {

  @Builder
  public MarketOrder(int orderId, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    super(OrderType.ACTIVE_MARKET, orderId, clientId, direction, quantity, timeInForce, ticker);
  }
}
