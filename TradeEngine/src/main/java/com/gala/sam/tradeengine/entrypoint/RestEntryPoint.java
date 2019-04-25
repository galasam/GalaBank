package com.gala.sam.tradeengine.entrypoint;

import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeengine.domain.PublicMarketStatus;
import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.service.MarketService;
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
      return OrderRequestResponse.success();
    } else {
      log.info("Order request failed to be entered into Trading Engine: {}", order.toString());
      return OrderRequestResponse.error();
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

  @PostMapping("reset")
  public void reset() {
    marketService.reset();
  }

}
