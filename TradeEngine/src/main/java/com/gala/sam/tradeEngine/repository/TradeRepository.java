package com.gala.sam.tradeEngine.repository;

import com.gala.sam.tradeEngine.domain.Trade;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TradeRepository extends CrudRepository<Trade, Integer> {

    List<Trade> findByMatchPrice(float matchPrice);

}
