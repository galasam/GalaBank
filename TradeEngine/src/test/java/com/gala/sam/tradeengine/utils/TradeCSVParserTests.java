package com.gala.sam.tradeengine.utils;

import com.gala.sam.tradeengine.UnitTest;
import com.gala.sam.tradeengine.domain.Trade;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TradeCSVParserTests {

  private final static String csvOutputHeader = "BUY ORDER,SELL ORDER,MATCH QTY,MATCH PRICE,TICKER";

  @Test
  public void canEncodeTradeToCSV() {
    //Given: A trade object
    final Trade tradeInput = Trade.builder()
        .buyOrder(64)
        .sellOrder(118)
        .matchPrice(1.5f)
        .matchQuantity(451)
        .ticker("FRED")
        .build();

    final String tradeOutput = "64,118,451,1.5,FRED";

    final List<String> outputTest = new ArrayList<>();
    outputTest.add(csvOutputHeader);
    outputTest.add(tradeOutput);

    final List<Trade> inputTrades = new LinkedList<>();
    inputTrades.add(tradeInput);

    //When: the trade is encoded in to CSV
    final List<String> output = TradeCSVParser.encodeCSV(inputTrades);

    //Then: the returned CSV should be correct
    Assert.assertEquals("Encoder should encode a trade correctly", output, outputTest);
  }

}
