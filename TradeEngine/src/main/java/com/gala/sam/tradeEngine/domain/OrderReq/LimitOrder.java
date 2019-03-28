package com.gala.sam.tradeEngine.domain.OrderReq;

import com.gala.sam.tradeEngine.domain.ConcreteOrder.Order;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class LimitOrder extends ActiveOrder {

  float limit;

  @Builder
  public LimitOrder(int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker, float limit) {
    super(OrderType.ACTIVE_LIMIT, clientId, direction, quantity, timeInForce, ticker);
    this.limit = limit;
  }

  public boolean limitMatches(LimitOrder other) {
    if (getDirection().equals(DIRECTION.BUY)) {
      return getLimit() >= other.getLimit();
    } else if (getDirection().equals(DIRECTION.SELL)) {
      return getLimit() <= other.getLimit();
    } else {
      throw new UnsupportedOperationException("Order direction not supported");
    }
  }

  @Override
  public Order toConcrete(int orderId) {
    return com.gala.sam.tradeEngine.domain.ConcreteOrder.LimitOrder.builder()
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
