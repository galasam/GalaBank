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
@Entity(name = "StopMarketOrder")
@DiscriminatorValue("StopMarketOrder")
public class StopMarketOrder extends AbstractStopOrder {

  private static final OrderType ORDER_TYPE = OrderType.STOP_MARKET;

  @Builder
  public StopMarketOrder(int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker, float triggerPrice) {
    super(ORDER_TYPE, orderId, clientId, direction, quantity, timeInForce, ticker, triggerPrice);
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
