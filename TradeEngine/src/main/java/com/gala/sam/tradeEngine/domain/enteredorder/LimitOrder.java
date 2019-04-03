package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Entity(name = "LimitOrderRequest")
@DiscriminatorValue("LimitOrderRequest")
public class LimitOrder extends AbstractActiveOrder {

  @Column(name = "limit_price")
  float limit;

  @Builder
  public LimitOrder(int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker, float limit) {
    super(OrderType.ACTIVE_LIMIT, orderId, clientId, direction, quantity, timeInForce, ticker);
    this.limit = limit;
  }

  public boolean limitMatches(LimitOrder other) {
    if (getDirection().equals(Direction.BUY)) {
      return getLimit() >= other.getLimit();
    } else if (getDirection().equals(Direction.SELL)) {
      return getLimit() <= other.getLimit();
    } else {
      throw new UnsupportedOperationException("orderrequest direction not supported");
    }
  }

}
