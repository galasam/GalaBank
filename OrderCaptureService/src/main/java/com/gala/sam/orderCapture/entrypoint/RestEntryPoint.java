package com.gala.sam.orderCapture.entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.gala.sam.orderCapture.service.TradeEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RestEntryPoint {

  @Autowired
  final TradeEngineService tradeEngineService;

  @PostMapping("/enter-order")
  public JsonNode enterOrder(@RequestBody String csvInput) {
    JsonNode order = tradeEngineService.enterOrder(csvInput);
    log.info("Order Entered in to Trade Engine: {}", order);
    return order;
  }

}
