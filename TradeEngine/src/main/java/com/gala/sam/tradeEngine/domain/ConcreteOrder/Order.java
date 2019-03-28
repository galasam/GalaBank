package com.gala.sam.tradeEngine.domain.ConcreteOrder;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.OrderType;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;

import javax.persistence.*;

@Data
@NonFinal
@Table(name = "orders")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Order_Type")
@NoArgsConstructor
public abstract class Order {

    OrderType type;

    @Id @Column
    int orderId;
    @Column
    int clientId;
    @Column
    DIRECTION direction;
    @Column
    int quantity;
    @Column
    int quantityRemaining;
    @Column
    TIME_IN_FORCE timeInForce;
    @Column
    String ticker;

    public Order(OrderType type, int orderId, int clientId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
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
        if(reduction > quantityRemaining) {
            throw new IllegalArgumentException("Reduction: " + Integer.toString(reduction)
                    + " larger than remaining quantity: " + Integer.toString(quantityRemaining));
        } else {
            quantityRemaining -= reduction;
        }
    }
}
