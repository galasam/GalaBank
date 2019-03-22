package com.gala.sam.tradeEngine.domain;

import lombok.Getter;

@Getter
public class Order {

    public enum OrderType {STOP, READY_LIMIT, READY_MARKET}

    OrderType type;

}
