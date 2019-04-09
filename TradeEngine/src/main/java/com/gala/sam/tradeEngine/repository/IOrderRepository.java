package com.gala.sam.tradeEngine.repository;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import org.springframework.data.repository.CrudRepository;

public interface IOrderRepository extends CrudRepository<AbstractOrder, Integer> {

}
