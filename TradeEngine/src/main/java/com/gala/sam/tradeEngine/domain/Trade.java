package com.gala.sam.tradeEngine.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    int buyOrder;
    int sellOrder;
    String ticker;
    int matchQuantity;
    float matchPrice;

}
