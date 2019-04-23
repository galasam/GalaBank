package com.gala.sam.tradeengine.utils;

import com.gala.sam.tradeengine.domain.Trade;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeCSVParser {
  
  static final String BUY_ORDER = "BUY ORDER";
  static final String SELL_ORDER = "SELL ORDER";
  static final String MATCH_QTY = "MATCH QTY";
  static final String MATCH_PRICE = "MATCH PRICE";
  static final String TICKER = "TICKER";

  private final static Map<String, Integer> HEADINGS = new TreeMap<>();
  private final static String OUTPUT_HEADER = String
      .join(",", BUY_ORDER, SELL_ORDER, MATCH_QTY, MATCH_PRICE, TICKER);

  static {
    HEADINGS.put(BUY_ORDER, 0);
    HEADINGS.put(SELL_ORDER, 1);
    HEADINGS.put(MATCH_QTY, 2);
    HEADINGS.put(MATCH_PRICE, 3);
    HEADINGS.put(TICKER, 4);
  }

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
        Float.toString(output.getMatchPrice()),
        output.getTicker());
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

    final int buyOrderId = Integer.parseInt(values[HEADINGS.get(BUY_ORDER)]);
    final int sellOrderId = Integer.parseInt(values[HEADINGS.get(SELL_ORDER)]);
    final int quantity = Integer.parseInt(values[HEADINGS.get(MATCH_QTY)]);
    final float price = Float.parseFloat(values[HEADINGS.get(MATCH_PRICE)]);
    final String ticker = values[HEADINGS.get(TICKER)];
    return Trade.builder()
        .buyOrder(buyOrderId)
        .sellOrder(sellOrderId)
        .matchQuantity(quantity)
        .matchPrice(price)
        .ticker(ticker)
        .build();
  }
}
