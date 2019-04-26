package com.gala.sam.tradeengine.utils;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractActiveOrder;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeengine.domain.enteredorder.MarketOrder;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class AbstractActiveOrderDeserializerTest {

  @Test
  public void deserializeLimitOrder() throws IOException {
    //Given: json Limit order
    LimitOrder referenceOrder = LimitOrder.builder()
        .orderId(1)
        .clientId(2)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("GREGGS")
        .limit(1.0f)
        .build();

    JsonParser jsonParser = getJsonParser(referenceOrder);

    //When: it is deserialized
    AbstractActiveOrder order = new AbstractActiveOrderDeserializer().deserialize(jsonParser, null);

    //Then: it creates the correct order
    Assert.assertEquals("Limit order is correct", referenceOrder, order);
  }

  @Test
  public void deserializeMarketOrder() throws IOException {
    //Given: json Limit order
    MarketOrder referenceOrder = MarketOrder.builder()
        .orderId(1)
        .clientId(2)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("GREGGS")
        .build();

    JsonParser jsonParser = getJsonParser(referenceOrder);

    //When: it is deserialized
    AbstractActiveOrder order = new AbstractActiveOrderDeserializer().deserialize(jsonParser, null);

    //Then: it creates the correct order
    Assert.assertEquals("Market order is correct", referenceOrder, order);
  }

  private JsonParser getJsonParser(AbstractOrder order) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonRoot = mapper.valueToTree(order);
    ObjectCodec codec = mock(ObjectCodec.class);
    JsonParser jsonParser = mock(JsonParser.class);
    given(codec.readTree(jsonParser)).willReturn(jsonRoot);
    given(jsonParser.getCodec()).willReturn(codec);
    return jsonParser;
  }

}