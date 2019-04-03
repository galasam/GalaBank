package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TIME_IN_FORCE;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity(name = "StopOrderRequest")
@DiscriminatorValue("StopOrderRequest")
@NoArgsConstructor
public abstract class AbstractStopOrder extends AbstractOrder {

  @Column
  float triggerPrice;

  public AbstractStopOrder(int orderId, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
    super(OrderType.STOP, orderId, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public float getTriggerPrice() {
    return triggerPrice;
  }

  public abstract AbstractActiveOrder toActiveOrder();

}
