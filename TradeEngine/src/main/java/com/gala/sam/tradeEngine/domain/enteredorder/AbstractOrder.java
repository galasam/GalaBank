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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "orders")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Order_Type")
public abstract class AbstractOrder {

  OrderType type;

  @Id
  @Column
  private int orderId;
  @Column
  private int clientId;
  @Column
  private Direction direction;
  @Column
  private int quantity;
  @Column
  private int quantityRemaining;
  @Column
  private TimeInForce timeInForce;
  @Column
  private String ticker;

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
      log.error(
          "Cannot reduce quantity remaining since reduction {} is larger than initial value {}",
          reduction, quantityRemaining);
      throw new IllegalArgumentException("Reduction: " + reduction
          + " larger than remaining quantity: " + quantityRemaining);
    } else {
      quantityRemaining -= reduction;
    }
  }
}

