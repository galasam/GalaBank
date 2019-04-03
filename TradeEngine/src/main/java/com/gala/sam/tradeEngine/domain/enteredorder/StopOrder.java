package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.DIRECTION;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.OrderRequest.TIME_IN_FORCE;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity(name = "StopOrderRequest")
@DiscriminatorValue("StopOrderRequest")
@NoArgsConstructor
public abstract class StopOrder extends Order {

  @Column
  float triggerPrice;

  public StopOrder(int orderId, int clientId, DIRECTION direction, int quantity,
      TIME_IN_FORCE timeInForce, String ticker, float triggerPrice) {
    super(OrderType.STOP, orderId, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public float getTriggerPrice() {
    return triggerPrice;
  }

  public abstract ActiveOrder toActiveOrder();

}
