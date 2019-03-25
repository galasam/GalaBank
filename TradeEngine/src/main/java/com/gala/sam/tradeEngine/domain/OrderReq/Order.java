package com.gala.sam.tradeEngine.domain.OrderReq;

public abstract class Order {

    abstract public com.gala.sam.tradeEngine.domain.ConcreteOrder.Order toConcrete(int orderId);

}
