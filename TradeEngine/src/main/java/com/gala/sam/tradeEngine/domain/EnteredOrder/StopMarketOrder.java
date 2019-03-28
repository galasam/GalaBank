package com.gala.sam.tradeEngine.domain.EnteredOrder;

import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.OrderReq.TIME_IN_FORCE;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(name = "StopMarketOrderReq")
@DiscriminatorValue("StopMarketOrderReq")
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
