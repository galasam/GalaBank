package com.gala.sam.tradeengine.repository;

import com.gala.sam.tradeengine.domain.Trade;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ITradeRepository extends CrudRepository<Trade, Integer> {

  List<Trade> findByMatchPrice(float matchPrice);

}
