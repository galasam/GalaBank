package com.gala.sam.tradeengine.utils;

import com.gala.sam.orderrequestlibrary.OrderCSVParser;
import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.orderrequestlibrary.orderrequest.StopLimitOrderRequest;
import com.gala.sam.tradeengine.utils.TradeCSVParser;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CSVTests {

  private final static String csvInputHeader = "ORDER ID,GROUP ID,DIRECTION,QUANTITY,TICKER,TYPE,LIMIT PRICE,TIME IN FORCE,TRIGGER PRICE";
  private final static String csvOutputHeader = "BUY ORDER,SELL ORDER,MATCH QTY,MATCH PRICE,TICKER";

  @Test
  public void canDecodeCSVStopLimitOrder() {

    //Given: A CSV string
    final String limitOrderInput = "1,1,BUY,999,Fred,STOP-LIMIT,3.14,GTC,666";
    final AbstractOrderRequest limitOrderOutput = StopLimitOrderRequest.builder()
        .triggerPrice(666)
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(TimeInForce.GTC)
        .build();

    final List<String> csvInput = new ArrayList<>();
    csvInput.add(csvInputHeader);
    csvInput.add(limitOrderInput);

    //When: CSV is decoded
    List<AbstractOrderRequest> orders = OrderCSVParser.decodeCSV(csvInput);

    //Then: A single order should be returned correctly
    Assert.assertEquals("Decoder should decode a stop limit order", 1, orders.size());
    Assert.assertEquals("Decoder should decode a stop limit order correctly", orders.get(0),
        limitOrderOutput);
  }

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
