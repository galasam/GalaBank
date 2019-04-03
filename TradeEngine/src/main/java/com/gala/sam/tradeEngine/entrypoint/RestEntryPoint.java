package com.gala.sam.tradeEngine.entrypoint;

import com.gala.sam.tradeEngine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.domain.PublicMarketStatus;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.utils.OrderCSVParser;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class RestEntryPoint {


  final MarketService marketService;

  @PostMapping("/enter-order")
  public AbstractOrder enterOrder(
      @RequestBody String csvInput) {
    final Stream<String> inputRows = Pattern.compile("\n").splitAsStream(csvInput);
    final List<AbstractOrderRequest> orders = OrderCSVParser.decodeCSV(inputRows);
    final AbstractOrderRequest order = orders.get(0);
    AbstractOrder enteredOrder = marketService.enterOrder(order);
    log.info("orderrequest entered into Trading Engine: {}", enteredOrder);
    return enteredOrder;
  }

  @GetMapping("trades")
  public List<Trade> trades() {
    return marketService.getAllMatchedTrades();
  }

  @GetMapping("status")
  public PublicMarketStatus status() {
    return marketService.getStatus();
  }

}
