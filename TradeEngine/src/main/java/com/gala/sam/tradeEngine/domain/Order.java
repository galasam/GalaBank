package com.gala.sam.tradeEngine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.NonFinal;

@Data
@NonFinal
public abstract class Order {

    public Order(int orderId, int groupId, DIRECTION direction, int quantity, TIME_IN_FORCE timeInForce, String ticker) {
        this.orderId = orderId;
        this.groupId = groupId;
        this.direction = direction;
        this.quantity = quantity;
        this.quantityRemaining = this.quantity;
        this.timeInForce = timeInForce;
        this.ticker = ticker;
    }

    public enum DIRECTION {SELL, BUY}
    public enum TIME_IN_FORCE {FOK, GTC}

    int orderId;
    int groupId;
    DIRECTION direction;
    int quantity;
    int quantityRemaining;
    TIME_IN_FORCE timeInForce;
    String ticker;

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
