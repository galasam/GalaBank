package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "AbstractStopOrderRequest")
@DiscriminatorValue("AbstractStopOrderRequest")
public abstract class AbstractStopOrder extends AbstractOrder {

  @Column
  private float triggerPrice;

  public AbstractStopOrder(OrderType orderType, int orderId, int clientId, Direction direction,
      int quantity,
      TimeInForce timeInForce, String ticker, float triggerPrice) {
    super(orderType, orderId, clientId, direction, quantity, timeInForce, ticker);
    this.triggerPrice = triggerPrice;
  }

  public abstract AbstractActiveOrder toActiveOrder();

}
