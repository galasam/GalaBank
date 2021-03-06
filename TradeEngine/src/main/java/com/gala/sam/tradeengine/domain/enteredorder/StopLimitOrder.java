package com.gala.sam.tradeengine.domain.enteredorder;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "StopLimitOrder")
@DiscriminatorValue("StopLimitOrder")
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
