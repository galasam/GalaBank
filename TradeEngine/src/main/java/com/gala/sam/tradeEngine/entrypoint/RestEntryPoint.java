package com.gala.sam.tradeEngine.entrypoint;

import com.gala.sam.tradeEngine.domain.OrderRequestResponse;
import com.gala.sam.tradeEngine.domain.PublicMarketStatus;
import com.gala.sam.tradeEngine.domain.Trade;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.service.MarketService;
import java.util.List;
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
  public <T extends AbstractOrderRequest> OrderRequestResponse enterOrder(@RequestBody T order) {
    log.info("Order request received into Trading Engine: {}", order.toString());
    if (marketService.enterOrder(order).isPresent()) {
      log.info("Order request successfully entered into Trading Engine: {}", order.toString());
      return OrderRequestResponse.Success();
    } else {
      log.info("Order request failed to be entered into Trading Engine: {}", order.toString());
      return OrderRequestResponse.Error();
    }
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
