package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.OrderReq.*;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.DIRECTION;
import com.gala.sam.tradeEngine.domain.OrderReq.Order.TIME_IN_FORCE;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrderCSVParser {

  private final static Map<String, Integer> INPUT_HEADINGS = new TreeMap<>();
  static {
    INPUT_HEADINGS.put("ORDER ID", 0);
    INPUT_HEADINGS.put("GROUP ID", 1);
    INPUT_HEADINGS.put("DIRECTION", 2);
    INPUT_HEADINGS.put("QUANTITY", 3);
    INPUT_HEADINGS.put("TICKER", 4);
    INPUT_HEADINGS.put("TYPE", 5);
    INPUT_HEADINGS.put("LIMIT PRICE", 6);
    INPUT_HEADINGS.put("TIME IN FORCE", 7);
    INPUT_HEADINGS.put("TRIGGER PRICE", 8);
  }

  public static List<Order> decodeCSV(List<String> input) {
    return decodeCSV(input.stream());
  }

  public static List<Order> decodeCSV(Stream<String> input) {
    return input.skip(1)
        .map(OrderCSVParser::decodeCSVRow)
        .collect(Collectors.toList());
  }

  private static Order decodeCSVRow(String input) {
    final String[] values = input.split(",");

    final int orderId = Integer.parseInt(values[INPUT_HEADINGS.get("ORDER ID")]);
    final int groupId = Integer.parseInt(values[INPUT_HEADINGS.get("GROUP ID")]);
    final DIRECTION direction = DIRECTION.valueOf(values[INPUT_HEADINGS.get("DIRECTION")]);
    final int quantity = Integer.parseInt(values[INPUT_HEADINGS.get("QUANTITY")]);
    final String type = values[INPUT_HEADINGS.get("TYPE")];
    final TIME_IN_FORCE tif = TIME_IN_FORCE.valueOf(values[INPUT_HEADINGS.get("TIME IN FORCE")]);
    final String ticker = values[INPUT_HEADINGS.get("TICKER")];

    switch (type) {
      case "LIMIT":
        float limit = Float.parseFloat(values[INPUT_HEADINGS.get("LIMIT PRICE")]);
        return LimitOrder.builder()
                .groupId(groupId)
                .direction(direction)
                .quantity(quantity)
                .timeInForce(tif)
                .ticker(ticker)
                .limit(limit)
                .build();
      case "MARKET":
        return MarketOrder.builder()
                .groupId(groupId)
                .direction(direction)
                .quantity(quantity)
                .timeInForce(tif)
                .ticker(ticker)
                .build();
      case "STOP-LIMIT":
        limit = Float.parseFloat(values[INPUT_HEADINGS.get("LIMIT PRICE")]);
        float triggerPrice = Float.parseFloat(values[INPUT_HEADINGS.get("TRIGGER PRICE")]);
        return StopLimitOrder.builder()
                .groupId(groupId)
                .direction(direction)
                .quantity(quantity)
                .timeInForce(tif)
                .ticker(ticker)
                .limit(limit)
                .triggerPrice(triggerPrice)
                .build();
      case "STOP-MARKET":
        triggerPrice = Float.parseFloat(values[INPUT_HEADINGS.get("TRIGGER PRICE")]);
        return StopMarketOrder.builder()
                .groupId(groupId)
                .direction(direction)
                .quantity(quantity)
                .timeInForce(tif)
                .ticker(ticker)
                .triggerPrice(triggerPrice)
                .build();
      default:
        throw new UnsupportedOperationException(" Unsupported order type");
    }
  }

}
