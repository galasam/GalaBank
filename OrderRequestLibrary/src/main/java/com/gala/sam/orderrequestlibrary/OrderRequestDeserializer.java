package com.gala.sam.orderrequestlibrary;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.MarketOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.StopLimitOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.StopMarketOrderRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderRequestDeserializer extends StdDeserializer<AbstractOrderRequest> {

  public OrderRequestDeserializer() {
    this(null);
  }

  public OrderRequestDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public AbstractOrderRequest deserialize(JsonParser jsonParser,
      DeserializationContext ctx) throws IOException {
    JsonNode root = jsonParser.getCodec().readTree(jsonParser);
    OrderType orderType = OrderType.valueOf(root.get("type").asText());
    int clientId = root.get("clientId").asInt();
    Direction direction = Direction.valueOf(root.get("direction").asText());
    int quantity = root.get("quantity").asInt();
    TimeInForce timeInForce = TimeInForce.valueOf(root.get("timeInForce").asText());
    String ticker = root.get("ticker").asText();
    final float limit;
    final float triggerPrice;
    switch (orderType) {
      case ACTIVE_MARKET:
        return MarketOrderRequest.builder()
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(timeInForce)
            .ticker(ticker)
            .build();
      case ACTIVE_LIMIT:
        limit = (float) root.get("limit").asDouble();
        return LimitOrderRequest.builder()
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(timeInForce)
            .ticker(ticker)
            .limit(limit)
            .build();
      case STOP_MARKET:
        triggerPrice = (float) root.get("triggerPrice").asDouble();
        return StopMarketOrderRequest.builder()
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(timeInForce)
            .ticker(ticker)
            .triggerPrice(triggerPrice)
            .build();
      case STOP_LIMIT:
        limit = (float) root.get("limit").asDouble();
        triggerPrice = (float) root.get("triggerPrice").asDouble();
        return StopLimitOrderRequest.builder()
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(timeInForce)
            .ticker(ticker)
            .limit(limit)
            .triggerPrice(triggerPrice)
            .build();
      default:
        log.error("Order type {} is not supported so cannot deserialize order request", orderType);
        throw new UnsupportedOperationException();
    }
  }
}
