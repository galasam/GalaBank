package com.gala.sam.tradeengine.repository;

import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.repository.ITradeRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTests {

  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private ITradeRepository tradeRepository;

  @Test
  public void canSaveTradesToDatabase() {
    //Given: a trade
    Trade trade = Trade.builder()
        .buyOrder(1)
        .sellOrder(2)
        .matchQuantity(10)
        .matchPrice(1.2f)
        .ticker("Greggs")
        .build();

    //When: I save it to the repository
    tradeRepository.save(trade);

    //Then: it is in the table
    Trade savedTrade = entityManager.find(Trade.class, trade.getBuyOrder());
    Assert.assertEquals("Same trade is return from the database", trade, savedTrade);

  }

  @Test
  public void canRetreiveTradesFromDatabase() {
    //Given: a trade in the table
    Trade trade = Trade.builder()
        .buyOrder(1)
        .sellOrder(2)
        .matchQuantity(10)
        .matchPrice(1.2f)
        .ticker("Greggs")
        .build();

    entityManager.persist(trade);
    entityManager.flush();

    //When: I retrieve trades from the repository
    List<Trade> tradesFromDatabase = new ArrayList<>();
    tradeRepository.findAll().forEach(tradesFromDatabase::add);

    //Then: I get one trade that equals the given
    Assert.assertEquals("There is one trade in table", 1, tradesFromDatabase.size());
    Assert.assertEquals("Same trade is return from the database", trade, tradesFromDatabase.get(0));
  }

}
