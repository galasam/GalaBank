package com.gala.sam.tradeEngine.domain.orderrequest;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class LimitOrderRequest extends AbstractActiveOrderRequest {

  private static final OrderType ORDER_TYPE = OrderType.ACTIVE_LIMIT;

  float limit;

  @Builder
  public LimitOrderRequest(int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker, float limit) {
    super(ORDER_TYPE, clientId, direction, quantity, timeInForce, ticker);
    this.limit = limit;
  }

  public boolean limitMatches(LimitOrderRequest other) {
    if (getDirection().equals(Direction.BUY)) {
      return getLimit() >= other.getLimit();
    } else if (getDirection().equals(Direction.SELL)) {
      return getLimit() <= other.getLimit();
    } else {
      throw new UnsupportedOperationException("orderrequest direction not supported");
    }
  }

  @Override
  public AbstractOrder toConcrete(int orderId) {
    return com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder.builder()
        .orderId(orderId)
        .clientId(getClientId())
        .direction(getDirection())
        .quantity(getQuantity())
        .ticker(getTicker())
        .timeInForce(getTimeInForce())
        .limit(getLimit())
        .build();
  }
}
