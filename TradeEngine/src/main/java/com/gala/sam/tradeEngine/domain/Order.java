package com.gala.sam.tradeEngine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Order {

    public enum OrderType {STOP, READY_LIMIT, READY_MARKET}

    final OrderType type;

}
