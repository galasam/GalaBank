package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.TIME_IN_FORCE;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(name = "MarketOrderRequest")
@DiscriminatorValue("MarketOrderRequest")
public class MarketOrder extends ActiveOrder {

  @Builder
  public MarketOrder(int orderId, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker) {
    super(OrderType.ACTIVE_MARKET, orderId, clientId, direction, quantity, timeInForce, ticker);
  }
}
