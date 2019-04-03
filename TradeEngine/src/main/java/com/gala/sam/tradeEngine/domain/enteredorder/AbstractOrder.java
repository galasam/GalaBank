package com.gala.sam.tradeEngine.domain.enteredorder;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;

@Data
@NonFinal
@Table(name = "orders")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Order_Type")
@NoArgsConstructor
public abstract class AbstractOrder {

  OrderType type;

  @Id
  @Column
  int orderId;
  @Column
  int clientId;
  @Column
  Direction direction;
  @Column
  int quantity;
  @Column
  int quantityRemaining;
  @Column
  TimeInForce timeInForce;
  @Column
  String ticker;

  public AbstractOrder(OrderType type, int orderId, int clientId, Direction direction, int quantity,
      TimeInForce timeInForce, String ticker) {
    this.type = type;
    this.orderId = orderId;
    this.clientId = clientId;
    this.direction = direction;
    this.quantity = quantity;
    this.quantityRemaining = this.quantity;
    this.timeInForce = timeInForce;
    this.ticker = ticker;
  }

  public boolean isFullyFulfilled() {
    return quantityRemaining == 0;
  }

  public void reduceQuantityRemaining(int reduction) {
    if (reduction > quantityRemaining) {
      throw new IllegalArgumentException("Reduction: " + Integer.toString(reduction)
          + " larger than remaining quantity: " + Integer.toString(quantityRemaining));
    } else {
      quantityRemaining -= reduction;
    }
  }
}
