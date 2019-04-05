package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.LimitOrderRequest;
import com.gala.sam.tradeEngine.domain.orderrequest.MarketOrderRequest;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.domain.orderrequest.StopLimitOrderRequest;
import com.gala.sam.tradeEngine.domain.orderrequest.StopMarketOrderRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderCSVParser {

  private final static String ORDER_ID = "ORDER ID";
  private final static String GROUP_ID = "GROUP ID";
  private final static String DIRECTION = "DIRECTION";
  private final static String QUANTITY = "QUANTITY";
  private final static String TICKER = "TICKER";
  private final static String TYPE = "TYPE";
  private final static String LIMIT_PRICE = "LIMIT PRICE";
  private final static String TIME_IN_FORCE = "TIME IN FORCE";
  private final static String TRIGGER_PRICE = "TRIGGER PRICE";

  private final static String TYPE_LIMIT = "LIMIT";
  private final static String TYPE_MARKET = "MARKET";
  private final static String TYPE_STOP_LIMIT = "STOP-LIMIT";
  private final static String TYPE_STOP_MARKET = "STOP-MARKET";

  private final static String CSV_DELIMETER = ",";

  private final static Map<String, Integer> INPUT_HEADINGS = new TreeMap<>();

  static {
    INPUT_HEADINGS.put(ORDER_ID, 0);
    INPUT_HEADINGS.put(GROUP_ID, 1);
    INPUT_HEADINGS.put(DIRECTION, 2);
    INPUT_HEADINGS.put(QUANTITY, 3);
    INPUT_HEADINGS.put(TICKER, 4);
    INPUT_HEADINGS.put(TYPE, 5);
    INPUT_HEADINGS.put(LIMIT_PRICE, 6);
    INPUT_HEADINGS.put(TIME_IN_FORCE, 7);
    INPUT_HEADINGS.put(TRIGGER_PRICE, 8);
  }

  public static List<AbstractOrderRequest> decodeCSV(List<String> input) {
    return decodeCSV(input.stream());
  }

  public static List<AbstractOrderRequest> decodeCSV(Stream<String> input) {
    return input.skip(1)
        .map(OrderCSVParser::decodeCSVRow)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  private static Optional<AbstractOrderRequest> decodeCSVRow(String input) {
    final String[] values = input.split(CSV_DELIMETER);

    final int orderId = Integer.parseInt(values[INPUT_HEADINGS.get(ORDER_ID)]);
    final int clientId = Integer.parseInt(values[INPUT_HEADINGS.get(GROUP_ID)]);
    final Direction direction = Direction.valueOf(values[INPUT_HEADINGS.get(DIRECTION)]);
    final int quantity = Integer.parseInt(values[INPUT_HEADINGS.get(QUANTITY)]);
    final String type = values[INPUT_HEADINGS.get(TYPE)];
    final TimeInForce tif = TimeInForce.valueOf(values[INPUT_HEADINGS.get(TIME_IN_FORCE)]);
    final String ticker = values[INPUT_HEADINGS.get(TICKER)];

    switch (type) {
      case TYPE_LIMIT:
        float limit = Float.parseFloat(values[INPUT_HEADINGS.get(LIMIT_PRICE)]);
        return Optional.of(LimitOrderRequest.builder()
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(tif)
            .ticker(ticker)
            .limit(limit)
            .build());
      case TYPE_MARKET:
        return Optional.of(MarketOrderRequest.builder()
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(tif)
            .ticker(ticker)
            .build());
      case TYPE_STOP_LIMIT:
        limit = Float.parseFloat(values[INPUT_HEADINGS.get(LIMIT_PRICE)]);
        float triggerPrice = Float.parseFloat(values[INPUT_HEADINGS.get(TRIGGER_PRICE)]);
        return Optional.of(StopLimitOrderRequest.builder()
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(tif)
            .ticker(ticker)
            .limit(limit)
            .triggerPrice(triggerPrice)
            .build());
      case TYPE_STOP_MARKET:
        triggerPrice = Float.parseFloat(values[INPUT_HEADINGS.get(TRIGGER_PRICE)]);
        return Optional.of(StopMarketOrderRequest.builder()
            .clientId(clientId)
            .direction(direction)
            .quantity(quantity)
            .timeInForce(tif)
            .ticker(ticker)
            .triggerPrice(triggerPrice)
            .build());
      default:
        log.error("Could not parse order since due to unsupported type: {}", type);
        return Optional.empty();
    }
  }

}
