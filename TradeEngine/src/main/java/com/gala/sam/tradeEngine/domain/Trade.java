package com.gala.sam.tradeEngine.domain;

import lombok.Builder;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Value
@Builder
@Entity
public class Trade {

    @Id
    int buyOrder;
    int sellOrder;
    int matchQuantity;
    float matchPrice;

}
