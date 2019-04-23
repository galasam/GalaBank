package com.gala.sam.tradeengine.repository;

import com.gala.sam.tradeengine.domain.enteredorder.AbstractOrder;
import org.springframework.data.repository.CrudRepository;

public interface IOrderRepository extends CrudRepository<AbstractOrder, Integer> {

}
