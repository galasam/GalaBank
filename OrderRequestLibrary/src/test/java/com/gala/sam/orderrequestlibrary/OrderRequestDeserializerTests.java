package com.gala.sam.orderrequestlibrary;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.MarketOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.StopLimitOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.StopMarketOrderRequest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class OrderRequestDeserializerTests {

  @Test
  public void canDeserializeMarketOrderRequest() throws IOException {

    //Given: json parser that returns a json object tree for a market order request
    AbstractOrderRequest orderRequest = MarketOrderRequest.builder()
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("Greggs")
        .build();

    JsonParser jsonParser = getJsonParser(orderRequest);

    //When: it is deserialized
    OrderRequestDeserializer deserializer = new OrderRequestDeserializer();
    AbstractOrderRequest output = deserializer.deserialize(jsonParser, null);

    //Then: the market order request is returned correctly
    Assert.assertEquals("Order request is correct", orderRequest, output);

  }

  @Test
  public void canDeserializeLimitOrderRequest() throws IOException {

    //Given: json parser that returns a json object tree for a market order request
    AbstractOrderRequest orderRequest = LimitOrderRequest.builder()
        .limit(1.0f)
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("Greggs")
        .build();

    JsonParser jsonParser = getJsonParser(orderRequest);

    //When: it is deserialized
    OrderRequestDeserializer deserializer = new OrderRequestDeserializer();
    AbstractOrderRequest output = deserializer.deserialize(jsonParser, null);

    //Then: the market order request is returned correctly
    Assert.assertEquals("Order request is correct", orderRequest, output);

  }

  @Test
  public void canDeserializeStopMarketOrderRequest() throws IOException {

    //Given: json parser that returns a json object tree for a market order request
    AbstractOrderRequest orderRequest = StopMarketOrderRequest.builder()
        .triggerPrice(1.0f)
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("Greggs")
        .build();

    JsonParser jsonParser = getJsonParser(orderRequest);

    //When: it is deserialized
    OrderRequestDeserializer deserializer = new OrderRequestDeserializer();
    AbstractOrderRequest output = deserializer.deserialize(jsonParser, null);

    //Then: the market order request is returned correctly
    Assert.assertEquals("Order request is correct", orderRequest, output);

  }

  @Test
  public void canDeserializeStopLimitOrderRequest() throws IOException {

    //Given: json parser that returns a json object tree for a market order request
    AbstractOrderRequest orderRequest = StopLimitOrderRequest.builder()
        .triggerPrice(10.0f)
        .limit(1.0f)
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("Greggs")
        .build();

    JsonParser jsonParser = getJsonParser(orderRequest);

    //When: it is deserialized
    OrderRequestDeserializer deserializer = new OrderRequestDeserializer();
    AbstractOrderRequest output = deserializer.deserialize(jsonParser, null);

    //Then: the market order request is returned correctly
    Assert.assertEquals("Order request is correct", orderRequest, output);

  }

  @Test
  public void doesNotDecodeIfTypeIsNotRecognised() throws IOException {

    //Given: json parser that returns a json object tree for a market order request
    AbstractOrderRequest orderRequest = StopLimitOrderRequest.builder()
        .triggerPrice(10.0f)
        .limit(1.0f)
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("Greggs")
        .build();

    JsonParser jsonParser = getJsonParser(orderRequest);

    //When: it is deserialized
    OrderRequestDeserializer deserializer = new OrderRequestDeserializer();
    AbstractOrderRequest output = deserializer.deserialize(jsonParser, null);

    //Then: the market order request is returned correctly
    Assert.assertEquals("Order request is correct", orderRequest, output);
  }

  private JsonParser getJsonParser(AbstractOrderRequest orderRequest) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonRoot = mapper.valueToTree(orderRequest);
    ObjectCodec codec = mock(ObjectCodec.class);
    JsonParser jsonParser = mock(JsonParser.class);
    given(codec.readTree(jsonParser)).willReturn(jsonRoot);
    given(jsonParser.getCodec()).willReturn(codec);
    return jsonParser;
  }

}
