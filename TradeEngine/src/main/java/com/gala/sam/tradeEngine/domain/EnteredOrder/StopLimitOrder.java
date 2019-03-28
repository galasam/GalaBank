package com.gala.sam.tradeEngine.domain.EnteredOrder;

import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.TIME_IN_FORCE;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "StopLimitOrderReq")
@DiscriminatorValue("StopLimitOrderReq")
public class StopLimitOrder extends StopOrder {

  @Column(name = "limit_price")
  float limit;

  @Builder
  public StopLimitOrder(int orderId, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker, float triggerPrice, float limit) {
    super(orderId, clientId, direction, quantity, timeInForce, ticker, triggerPrice);
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