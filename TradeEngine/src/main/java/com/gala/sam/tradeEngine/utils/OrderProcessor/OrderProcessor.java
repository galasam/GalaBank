package com.gala.sam.tradeEngine.utils.OrderProcessor;

import com.gala.sam.tradeEngine.domain.Order;

public interface OrderProcessor {
    <T extends Order> void process(T order);
}
