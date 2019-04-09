package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.utils.exception.OrderDirectionNotSupportedException;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
@Getter @Setter @NoArgsConstructor
@Entity(name = "LimitOrderRequest") @DiscriminatorValue("LimitOrderRequest")
public class LimitOrder extends AbstractActiveOrder {

  private static final OrderType ORDER_TYPE = OrderType.ACTIVE_LIMIT;

  @Column(name = "limit_price")
  float limit;

  @Builder
  public LimitOrder(int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker, float limit) {
    super(ORDER_TYPE, orderId, clientId, direction, quantity, timeInForce, ticker);
    this.limit = limit;
  }

  public boolean limitMatches(LimitOrder other) throws OrderDirectionNotSupportedException {
    if (getDirection().equals(Direction.BUY)) {
      return getLimit() >= other.getLimit();
    } else if (getDirection().equals(Direction.SELL)) {
      return getLimit() <= other.getLimit();
    } else {
      log.error("Order {} has unsupported direction: {} so cannot match", getOrderId(), getDirection().toString());
      throw new OrderDirectionNotSupportedException(getDirection());
    }
  }

}
