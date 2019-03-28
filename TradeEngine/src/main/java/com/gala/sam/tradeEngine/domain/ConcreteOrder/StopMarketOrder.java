package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(name = "StopMarketOrder")
@DiscriminatorValue("StopMarketOrder")
public class StopMarketOrder extends StopOrder {

  @Builder
  public StopMarketOrder(int orderId, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
    super(orderId, clientId, direction, quantity, timeInForce, ticker, triggerPrice);
  }

  @Override
  public MarketOrder toActiveOrder() {
    return MarketOrder.builder()
        .orderId(getOrderId())
        .direction(getDirection())
        .quantity(getQuantity())
        .timeInForce(getTimeInForce())
        .ticker(getTicker())
        .build();
  }
}
