package com.gala.sam.tradeEngine.repository;

import com.gala.sam.tradeEngine.domain.EnteredOrder.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer> {

}