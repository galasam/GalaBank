package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(name = "MarketOrderRequest")
@DiscriminatorValue("MarketOrderRequest")
public class MarketOrder extends AbstractActiveOrder {

  @Builder
  public MarketOrder(int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker) {
    super(OrderType.ACTIVE_MARKET, orderId, clientId, direction, quantity, timeInForce, ticker);
  }
}
