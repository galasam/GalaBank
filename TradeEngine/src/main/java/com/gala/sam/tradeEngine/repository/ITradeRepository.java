package com.gala.sam.tradeEngine.repository;

import com.gala.sam.tradeEngine.domain.Trade;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ITradeRepository extends CrudRepository<Trade, Integer> {

  List<Trade> findByMatchPrice(float matchPrice);

}
