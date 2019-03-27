package com.gala.sam.tradeEngine.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Trade {

    int buyOrder;
    int sellOrder;
    String ticker;
    int matchQuantity;
    float matchPrice;

}
