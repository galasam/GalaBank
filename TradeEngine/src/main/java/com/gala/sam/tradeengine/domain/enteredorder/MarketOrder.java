package com.gala.sam.tradeengine.domain.enteredorder;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "MarketOrderRequest")
@DiscriminatorValue("MarketOrderRequest")
public class MarketOrder extends AbstractActiveOrder {

  private static final OrderType ORDER_TYPE = OrderType.ACTIVE_MARKET;

  @Builder
  public MarketOrder(int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker) {
    super(ORDER_TYPE, orderId, clientId, direction, quantity, timeInForce, ticker);
  }
}
