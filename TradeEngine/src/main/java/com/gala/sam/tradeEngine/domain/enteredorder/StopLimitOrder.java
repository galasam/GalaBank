package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "StopLimitOrderRequest")
@DiscriminatorValue("StopLimitOrderRequest")
public class StopLimitOrder extends AbstractStopOrder {

  private static final OrderType ORDER_TYPE = OrderType.STOP_LIMIT;

  @Column(name = "limit_price")
  float limit;

  @Builder
  public StopLimitOrder(int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker, float triggerPrice, float limit) {
    super(ORDER_TYPE, orderId, clientId, direction, quantity, timeInForce, ticker, triggerPrice);
    this.limit = limit;
  }

  @Override
  public LimitOrder toActiveOrder() {
    return LimitOrder.builder()
        .orderId(getOrderId())
        .direction(getDirection())
        .quantity(getQuantity())
        .timeInForce(getTimeInForce())
        .ticker(getTicker())
        .limit(getLimit())
        .build();
  }
}
