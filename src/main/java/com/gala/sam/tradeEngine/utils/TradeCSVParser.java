package com.gala.sam.tradeEngine.utils;

import com.gala.sam.tradeEngine.domain.LimitOrder;
import com.gala.sam.tradeEngine.domain.MarketOrder;
import com.gala.sam.tradeEngine.domain.Order;
import com.gala.sam.tradeEngine.domain.ReadyOrder.DIRECTION;
import com.gala.sam.tradeEngine.domain.ReadyOrder.TIME_IN_FORCE;
import com.gala.sam.tradeEngine.domain.StopOrder;
import com.gala.sam.tradeEngine.domain.Trade;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradeCSVParser {

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



}
