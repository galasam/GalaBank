package com.gala.sam.tradeEngine.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractActiveOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.MarketOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.OrderType;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractActiveOrderDeserializer extends StdDeserializer<AbstractActiveOrder> {

  public AbstractActiveOrderDeserializer() {
    this(null);
  }

  public AbstractActiveOrderDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public AbstractActiveOrder deserialize(JsonParser jsonParser,
      DeserializationContext ctx) throws IOException, JsonProcessingException {
    JsonNode root = jsonParser.getCodec().readTree(jsonParser);
    OrderType orderType = OrderType.valueOf(root.get("type").asText());
    int orderId = root.get("orderId").asInt();
    int clientId = root.get("clientId").asInt();
    Direction direction = Direction.valueOf(root.get("direction").asText());
    int quantity = root.get("quantity").asInt();
    TimeInForce timeInForce = TimeInForce.valueOf(root.get("timeInForce").asText());
    String ticker = root.get("ticker").asText();
    final float limit;
    switch (orderType) {
      case ACTIVE_MARKET:
        return MarketOrder.builder()
            .orderId(orderId)
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(timeInForce)
            .ticker(ticker)
            .build();
      case ACTIVE_LIMIT:
        limit = (float) root.get("limit").asDouble();
        return LimitOrder.builder()
            .orderId(orderId)
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(timeInForce)
            .ticker(ticker)
            .limit(limit)
            .build();
      default:
        log.error("Order type {} is not supported so cannot deserialize order request", orderType);
        throw new UnsupportedOperationException();
    }
  }
}
