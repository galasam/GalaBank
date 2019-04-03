package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity(name = "StopOrderRequest")
@DiscriminatorValue("StopOrderRequest")
@NoArgsConstructor
public abstract class AbstractStopOrder extends AbstractOrder {

  private static final OrderType ORDER_TYPE = OrderType.STOP;

  @Column
  float triggerPrice;

  public AbstractStopOrder(int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker, float triggerPrice) {
    super(ORDER_TYPE, orderId, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public float getTriggerPrice() {
    return triggerPrice;
  }

  public abstract AbstractActiveOrder toActiveOrder();

}
