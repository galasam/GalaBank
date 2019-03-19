package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.Trade;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradeCSVParser {

  private final static Map<String, Integer> HEADINGS = new TreeMap<>();
  static {
    HEADINGS.put("BUY ORDER", 0);
    HEADINGS.put("SELL ORDER", 1);
    HEADINGS.put("MATCH QTY", 2);
    HEADINGS.put("MATCH PRICE", 3);
  }
  private final static String OUTPUT_HEADER = String.join(",", "BUY ORDER", "SELL ORDER", "MATCH QTY", "MATCH PRICE");

  public static List<String> encodeCSV(List<Trade> output) {
    return encodeCSV(output.stream());
  }

  public static List<String> encodeCSV(Stream<Trade> output) {
    return Stream.concat(
        Stream.of(OUTPUT_HEADER),
        output.map(TradeCSVParser::encodeCSVRow)
    ).collect(Collectors.toList());
  }

  private static String encodeCSVRow(Trade output) {
    return String.join(",",
        Integer.toString(output.getBuyOrder()),
        Integer.toString(output.getSellOrder()),
        Integer.toString(output.getMatchQuantity()),
        Float.toString(output.getMatchPrice()));
  }


  public static List<Trade> decodeCSV(List<String> inputText) {
    return decodeCSV(inputText.stream());
  }

  private static List<Trade> decodeCSV(Stream<String> input) {
    return input.skip(1)
        .map(TradeCSVParser::decodeCSVRow)
        .collect(Collectors.toList());
  }

  private static Trade decodeCSVRow(String input) {
    final String[] values = input.split(",");

    final int buyOrderId = Integer.parseInt(values[HEADINGS.get("BUY ORDER")]);
    final int sellOrderId = Integer.parseInt(values[HEADINGS.get("SELL ORDER")]);
    final int quantity = Integer.parseInt(values[HEADINGS.get("MATCH QTY")]);
    final float price = Float.parseFloat(values[HEADINGS.get("MATCH PRICE")]);
    return Trade.builder()
        .buyOrder(buyOrderId)
        .sellOrder(sellOrderId)
        .matchQuantity(quantity)
        .matchPrice(price)
        .build();
  }
}
