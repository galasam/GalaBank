package com.gala.sam.tradeEngine.entrypoint;

import com.gala.sam.tradeEngine.domain.Order;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.CSVParser;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RestEntryPoint {

  final MarketService marketService;

  @PostMapping("/enter-order")
  public Order enterOrder(@RequestBody String csvInput) {
    final Stream<String> inputRows = Pattern.compile("\n").splitAsStream(csvInput);
    final List<Order> orders = CSVParser.decodeCSV(inputRows);
    final Order order = orders.get(0);
    marketService.enterOrder(order);
    return order;
  }

  @GetMapping("trades")
  public List<Trade> trades() {
    return marketService.getAllMatchedTrades();
  }

}
