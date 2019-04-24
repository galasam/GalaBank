package com.gala.sam.orderrequestlibrary;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.MarketOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.StopLimitOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.StopMarketOrderRequest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class OrderCSVParserTests {

  private final static String csvInputHeader = "ORDER ID,GROUP ID,DIRECTION,QUANTITY,TICKER,TYPE,LIMIT PRICE,TIME IN FORCE,TRIGGER PRICE";

  @Test
  public void canDecodeCSVMarketOrder() {

    //Given: A CSV string for a market order and its expected object
    final String orderInput = "1,1,BUY,999,Fred,MARKET,3.14,GTC,666";
    final String csvInput = csvInputHeader + "\n" + orderInput;

    final AbstractOrderRequest orderOutput = MarketOrderRequest.builder()
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(999)
        .ticker("Fred")
        .timeInForce(TimeInForce.GTC)
        .build();

    //When: CSV is decoded
    List<AbstractOrderRequest> orders = OrderCSVParser.decodeCSV(csvInput);

    //Then: A single order should be returned correctly
    Assert.assertEquals("Decoder should decode a stop limit order", 1, orders.size());
    Assert.assertEquals("Decoder should decode a stop limit order correctly", orderOutput,
        orders.get(0));
  }

  @Test
  public void canDecodeCSVLimitOrder() {

    //Given: A CSV string for a Limit order and its expected object
    final String orderInput = "1,1,BUY,999,Fred,LIMIT,3.14,GTC,666";
    final String csvInput = csvInputHeader + "\n" + orderInput;

    final AbstractOrderRequest orderOutput = LimitOrderRequest.builder()
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(TimeInForce.GTC)
        .build();

    //When: CSV is decoded
    List<AbstractOrderRequest> orders = OrderCSVParser.decodeCSV(csvInput);

    //Then: A single order should be returned correctly
    Assert.assertEquals("Decoder should decode a stop limit order", 1, orders.size());
    Assert.assertEquals("Decoder should decode a stop limit order correctly", orderOutput,
        orders.get(0));
  }

  @Test
  public void canDecodeCSVStopMarketOrder() {

    //Given: A CSV string for a Stop Market order and its expected object
    final String orderInput = "1,1,BUY,999,Fred,STOP-MARKET,3.14,GTC,666";
    final String csvInput = csvInputHeader + "\n" + orderInput;

    final AbstractOrderRequest orderOutput = StopMarketOrderRequest.builder()
        .triggerPrice(666)
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(999)
        .ticker("Fred")
        .timeInForce(TimeInForce.GTC)
        .build();

    //When: CSV is decoded
    List<AbstractOrderRequest> orders = OrderCSVParser.decodeCSV(csvInput);

    //Then: A single order should be returned correctly
    Assert.assertEquals("Decoder should decode a stop limit order", 1, orders.size());
    Assert.assertEquals("Decoder should decode a stop limit order correctly", orderOutput,
        orders.get(0));
  }

  @Test
  public void canDecodeCSVStopLimitOrder() {

    //Given: A CSV string for a Stop Limit order and its expected object
    final String orderInput = "1,1,BUY,999,Fred,STOP-LIMIT,3.14,GTC,666";
    final String csvInput = csvInputHeader + "\n" + orderInput;

    final AbstractOrderRequest orderOutput = StopLimitOrderRequest.builder()
        .triggerPrice(666)
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(999)
        .ticker("Fred")
        .limit(3.14f)
        .timeInForce(TimeInForce.GTC)
        .build();

    //When: CSV is decoded
    List<AbstractOrderRequest> orders = OrderCSVParser.decodeCSV(csvInput);

    //Then: A single order should be returned correctly
    Assert.assertEquals("Decoder should decode a stop limit order", 1, orders.size());
    Assert.assertEquals("Decoder should decode a stop limit order correctly", orderOutput,
        orders.get(0));
  }

  @Test
  public void doesNotDecodeIfTypeIsNotRecognised() {

    //Given: A CSV string with invalid type
    final String orderInput = "1,1,BUY,999,Fred,MEERKAT,3.14,GTC,666";
    final String csvInput = csvInputHeader + "\n" + orderInput;

    //When: CSV is decoded
    List<AbstractOrderRequest> orders = OrderCSVParser.decodeCSV(csvInput);

    //Then: No orders should be returned.
    Assert.assertEquals("Decoder should decode a stop limit order", 0, orders.size());
  }

}
